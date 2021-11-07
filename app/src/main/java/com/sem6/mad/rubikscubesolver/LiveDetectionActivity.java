package com.sem6.mad.rubikscubesolver;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.catalinjurjiu.rubikdetector.RubikDetector;
import com.catalinjurjiu.rubikdetector.RubikDetectorUtils;
import com.catalinjurjiu.rubikdetector.config.DrawConfig;
import com.catalinjurjiu.rubikdetector.model.RubikFacelet;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class LiveDetectionActivity extends Activity implements SurfaceHolder.Callback {

    public static final int DEFAULT_PREVIEW_WIDTH = 1920;
    public static final int DEFAULT_PREVIEW_HEIGHT = 1080;
    public static final int DEFAULT_IMAGE_FORMAT = ImageFormat.NV21;

    private static final String TAG = LiveDetectionActivity.class.getSimpleName();
    private SurfaceHolder surfaceHolder;
    private ProcessingThread processingThread;
    public static Button btnCapture;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.w(TAG, "surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        //TODO of course, the logic here would need to be more complex we would know that the surface size can change
        //TODO however, strictly in this case, this does not happen. so we're safe with initializing the camera & rendering here
        processingThread.openCamera();
        processingThread.startCamera();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed. calling stop camera and rendering");
        processingThread.performCleanup();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_detection);
        setTheme(R.style.Theme_ModernDashbord);
        setTitle(null);
        surfaceHolder = ((SurfaceView) findViewById(R.id.camera_surface_view)).getHolder();
        surfaceHolder.addCallback(this);

        processingThread = new ProcessingThread("RubikProcessingThread", surfaceHolder);
        processingThread.start();

        btnCapture = findViewById(R.id.btn_capture);
//        LiveDetectionActivity.btnCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("brozz", "work clikc");
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy - cleanup.");
        surfaceHolder.removeCallback(this);
        try {
            processingThread.performCleanup();
            Log.d(TAG, "calling quit!");
            //after cleanup, call quit
            processingThread.quit();
            Log.d(TAG, "now calling join!");
            //then wait for the thread to finish
            processingThread.join();
            Log.d(TAG, "after join!");
        } catch (InterruptedException e) {
            Log.d(TAG, "onDestroy - exception when waiting for the processing thread to finish.", e);
        }
        Log.d(TAG, "calling super.onDestroy!");
        super.onDestroy();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        new MenuInflater(this).inflate(R.menu.menu_continuous_processing_activity, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.button_menu_change_resolution:
//                showChangeResolutionDialog();
//                break;
//            case R.id.button_menu_draw_from_java:
//                switchDrawingToJava();
//                break;
//            case R.id.button_menu_draw_from_cpp:
//                switchDrawingToCpp();
//                break;
//            case R.id.button_menu_toggle_drawing:
//                toggleDrawing();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void switchDrawingToJava() {
        String msg = processingThread.switchDrawingToJava();
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void switchDrawingToCpp() {
        String msg = processingThread.switchDrawingToCpp();
        Toast.makeText(this.getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void toggleDrawing() {
        processingThread.toggleDrawing();
    }

    private void showChangeResolutionDialog() {
        final List<Camera.Size> previewFormatSizes = processingThread.getValidCameraSizes();
        if (previewFormatSizes == null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Resolution picker")
                .setItems(availableSizesToStringArray(previewFormatSizes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        processingThread.updatePreviewSize(previewFormatSizes.get(i));
                    }
                })
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

    private String[] availableSizesToStringArray(List<Camera.Size> previewFormatSizes) {
        String[] result = new String[previewFormatSizes.size()];
        for (Camera.Size size : previewFormatSizes) {
            result[previewFormatSizes.indexOf(size)] = size.width + "x" + size.height;
        }
        return result;
    }
}

@SuppressWarnings("deprecation")
final class ProcessingThread extends HandlerThread implements Camera.PreviewCallback {

    private static final String TAG = ProcessingThread.class.getSimpleName();
    private static final int OPEN_CAMERA = 0;
    private static final int START_CAMERA = 1;
    private static final int PERFORM_CLEANUP = 2;
    private static final int UPDATE_PREVIEW_SIZE = 3;
    private static final int SWITCH_DRAWING_TO_CPP = 4;
    private static final int SWITCH_DRAWING_TO_JAVA = 5;
    private static final int REDUNDANT_TEXTURE_ID = 13242;
    private static final boolean IS_DEBUGGABLE = true;

    private final Object cleanupLock = new Object();
    private final SurfaceHolder surfaceHolder;

    private Handler backgroundHandler;
    private Camera camera;
    private Camera.Size previewSize;
    private int currentConfigPreviewFrameByteCount = -1;
    private List<Camera.Size> validPreviewFormatSizes;
    private RubikDetector rubikDetector;
    private Bitmap drawingBitmap;
    private ByteBuffer drawingBuffer;
    private Paint paint;
    private boolean drawing = true;
    private boolean drawingFromJava = false;
    private SurfaceTexture surfaceTexture;
    public String faceletcol = "";


    public  String[] cubeFaceColor = new String[7];;
    int count = 0;



    ProcessingThread(String name, SurfaceHolder surfaceHolder) {
        super(name);
        this.surfaceHolder = surfaceHolder;
        Arrays.fill(cubeFaceColor,"");

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame, data: " + data);
        if (data == null) {
            Log.w(TAG, "Received null data array, or a data array of wrong size, from camera. Do nothing.");
            return;

        }

        if (data.length != currentConfigPreviewFrameByteCount) {
            Log.w(TAG, "Received data array of wrong size from camera. Do nothing.");
            return;
        }

        Log.d(TAG, "onPreviewFrame, data buffer size: " + data.length);
        if (rubikDetector.isActive()) {
            renderFrameInternal(data);
        }
        camera.addCallbackBuffer(data);
    }

    void openCamera() {
        backgroundHandler.sendEmptyMessage(OPEN_CAMERA);
    }

    void startCamera() {
        backgroundHandler.sendEmptyMessage(START_CAMERA);
    }

    List<Camera.Size> getValidCameraSizes() {
        return validPreviewFormatSizes;
    }

    void updatePreviewSize(Camera.Size newPreviewSize) {
        Log.w(TAG, "#updatePreviewSize");
        Message msg = Message.obtain();
        msg.what = UPDATE_PREVIEW_SIZE;
        msg.arg1 = newPreviewSize.width;
        msg.arg2 = newPreviewSize.height;
        backgroundHandler.sendMessage(msg);
    }

    void performCleanup() {
        Log.d(TAG, "before sync area.");
        synchronized (cleanupLock) {
            Log.d(TAG, "sending PERFORM_CLEANUP to background hander, in sync area.");
            backgroundHandler.sendEmptyMessage(PERFORM_CLEANUP);
            Log.d(TAG, "called stop, starting to wait.");
            try {
                //wait for cleanup to happen
                cleanupLock.wait();
                Log.d(TAG, "after wait, cleanup finished!");
            } catch (InterruptedException e) {
                Log.d(TAG, "after wait, interrupted exception occurred!", e);
            } finally {
                Log.d(TAG, "cleanup finished!");
                //do I have to do anything here?
            }
        }
    }

    void toggleDrawing() {
        drawing = !drawing;
        if (drawing) {
            if (drawingFromJava) {
                switchDrawingToJava();
            } else {
                switchDrawingToCpp();
            }
        } else {
            switchDrawingToJava();
        }
    }

    String switchDrawingToJava() {
        if (drawing) {
            if (!drawingFromJava) {
                drawingFromJava = true;
                backgroundHandler.sendEmptyMessage(SWITCH_DRAWING_TO_JAVA);
                return "Switched drawing to Java";
            } else {
                return "Already drawing from Java!";
            }
        } else {
            return "Cannot draw from Java because drawing is toggled off!";
        }
    }

    String switchDrawingToCpp() {
        if (drawing) {
            if (drawingFromJava) {
                drawingFromJava = false;
                backgroundHandler.sendEmptyMessage(SWITCH_DRAWING_TO_CPP);
                return "Switched drawing to C++";
            } else {
                return "Already drawing from C++!";
            }
        } else {
            return "Cannot draw from C++ because drawing is toggled off!";
        }
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        this.backgroundHandler = new Handler(ProcessingThread.this.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case OPEN_CAMERA:
                        openCameraInternal();
                        break;
                    case START_CAMERA:
                        startCameraInternal();
                        break;
                    case PERFORM_CLEANUP:
                        performCleanupInternal();
                        break;
                    case UPDATE_PREVIEW_SIZE:
                        updatePreviewSizeInternal(msg.arg1, msg.arg2);
                        break;
                    case SWITCH_DRAWING_TO_JAVA:
                        switchDrawingToJavaInternal();
                        break;
                    case SWITCH_DRAWING_TO_CPP:
                        switchDrawingToCppInternal();
                        break;
                    default:
                        Log.d(TAG, "Handler default case:" + msg.what);
                }
            }
        };
    }

    private void openCameraInternal() {
        int cameraId = -1;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        if (cameraId == -1) {
            Log.d(TAG, "Couldn't find the main camera!");
            return;
        }
        camera = Camera.open(cameraId);
       // camera.setDisplayOrientation(90);
        Camera.Parameters cameraParameters = camera.getParameters();
        cameraParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        validPreviewFormatSizes = cameraParameters.getSupportedPreviewSizes();
        previewSize = findHighResValidPreviewSize(camera);
        cameraParameters.setPreviewSize(previewSize.width, previewSize.height);
        Log.d("brok","" + previewSize.width + " h: "+ previewSize.height);
        cameraParameters.setPreviewFormat(LiveDetectionActivity.DEFAULT_IMAGE_FORMAT);

        camera.setParameters(cameraParameters);

        try {
            surfaceTexture = new SurfaceTexture(REDUNDANT_TEXTURE_ID);
         //   camera.setDisplayOrientation(90);
            camera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            Log.w(TAG, "error creating the texture", e);
        }

        rubikDetector = new RubikDetector.Builder()
                .debuggable(IS_DEBUGGABLE)
                .drawConfig(DrawConfig.FilledCircles())
                .inputFrameSize(previewSize.width, previewSize.height)
                .inputFrameFormat(RubikDetectorUtils.convertAndroidImageFormat(LiveDetectionActivity.DEFAULT_IMAGE_FORMAT))
                .build();
        allocateAndSetBuffers();
    }

    private void startCameraInternal() {
       // camera.setDisplayOrientation(90);
        camera.startPreview();
    }

    private void updatePreviewSizeInternal(int newWidth, int newHeight) {
        Log.w(TAG, "#updatePreviewSize");
        camera.stopPreview();
        Log.w(TAG, "#updatePreviewSize preview stopped");
        Camera.Parameters params = camera.getParameters();
        params.setPreviewSize(newWidth, newHeight);
        camera.setParameters(params);
        previewSize = camera.new Size(newWidth, newHeight);

        rubikDetector.updateImageProperties(new RubikDetector.ImageProperties(newWidth, newHeight, RubikDetectorUtils.convertAndroidImageFormat(LiveDetectionActivity.DEFAULT_IMAGE_FORMAT)));
        //clear the previous buffer queue
        camera.setPreviewCallbackWithBuffer(null);
        camera.setPreviewCallback(null);
        allocateAndSetBuffers();
        camera.startPreview();
        Log.w(TAG, "#updatePreviewSize preview restarted");
    }

    private void performCleanupInternal() {
        Log.d(TAG, "processing thread before cleanup sync area.");
        synchronized (cleanupLock) {
            Log.d(TAG, "processing thread inside cleanup sync area.");
            rubikDetector.releaseResources();
            try {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
            } catch (RuntimeException e) {
                Log.d(TAG, "Error when stopping camera. Ignored.", e);
            } finally {
                Log.d(TAG, "processing thread inside cleanup sync area, cleanup performed, notifying.");
                cleanupLock.notify();
                Log.d(TAG, "processing thread inside cleanup sync area, cleanup performed, after notify.");
            }
        }
        Log.d(TAG, "processing thread inside cleanup sync area, cleanup performed, after sync area.");
    }

    private void switchDrawingToJavaInternal() {
        initializePaint();
        RubikDetector oldRubikDetector = rubikDetector;
        rubikDetector = new RubikDetector.Builder()
                .drawConfig(DrawConfig.DoNotDraw())
                .debuggable(IS_DEBUGGABLE)
                .inputFrameFormat(RubikDetectorUtils.convertAndroidImageFormat(LiveDetectionActivity.DEFAULT_IMAGE_FORMAT))
                .inputFrameSize(previewSize.width, previewSize.height)
                .build();
        oldRubikDetector.releaseResources();
    }

    private void switchDrawingToCppInternal() {
        paint = null;
        RubikDetector oldRubikDetector = rubikDetector;
        rubikDetector = new RubikDetector.Builder()
                .drawConfig(DrawConfig.FilledCircles())
                .debuggable(IS_DEBUGGABLE)
                .inputFrameFormat(RubikDetectorUtils.convertAndroidImageFormat(LiveDetectionActivity.DEFAULT_IMAGE_FORMAT))
                .inputFrameSize(previewSize.width, previewSize.height)
                .build();
        oldRubikDetector.releaseResources();
    }

    private void renderFrameInternal(byte[] data) {
     //   camera.setDisplayOrientation(90);
        Log.w(TAG, "renderFrameInternal");
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) {
            return;
        }

        Rect srcRect = new Rect(0, 0, previewSize.width, previewSize.height);

        ///////////////////////Adding button capture here//////////////////////////////

//        LiveDetectionActivity.btnCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("bro2", "click");
//            }
//        });

        RubikFacelet[][] facelets = rubikDetector.findCube(data);

        RubikFacelet[][] finalFacelets1 = facelets;



        LiveDetectionActivity.btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {

                Log.d("bro", "click" + count);

                //accept facelet
                if ( count <= 6 && finalFacelets1 != null)
                {

                    faceletcol =  getCubeColorString(finalFacelets1);

                    new LovelyTextInputDialog(view.getContext(), R.style.EditTextTintTheme)
                            .setTopColorRes(R.color.teal_200)
                            .setTitle("Check Facelet Colors")
                            .setMessage(faceletcol)
                            .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                                @Override
                                public void onTextInputConfirmed(String text) {

//                                    Toast.makeText(view.getContext(), faceletcol, Toast.LENGTH_SHORT).show();
//                                    cubeFaceColor[count] += faceletcol;
//                                    count++;

                                    if ( text.length() == 9)
                                    {
                                        faceletcol = text;
                                        Toast.makeText(view.getContext(), faceletcol, Toast.LENGTH_SHORT).show();
                                        cubeFaceColor[count] = faceletcol;
                                        Log.d("bro1", cubeFaceColor[count] );
                                        count++;

                                    }

                                    else{

                                        Log.d("bro1",faceletcol);
                                        cubeFaceColor[count] = faceletcol;
                                        Log.d("bro1", "in " + cubeFaceColor[count]);
                                        Toast.makeText(view.getContext(), faceletcol, Toast.LENGTH_SHORT).show();
                                        count++;
                                        Log.d("bro1","in here");
                                    }


                                }
                            })
                            .show();

                    if( count > 5)
                    {
                        String cube = cubeFaceColor[0] + cubeFaceColor[1] + cubeFaceColor[2] +
                                cubeFaceColor[3] + cubeFaceColor[4] + cubeFaceColor[5];
                        Log.d("broskii", cube);
                        Intent intent = new Intent(view.getContext(),
                                SolveCube.class);
                        intent.putExtra("cubeString",cube);

                        view.getContext().startActivity(intent);
                    }
                }
            }
        });



        drawingBuffer.rewind();
        drawingBuffer.put(data, rubikDetector.getResultFrameBufferOffset(), rubikDetector.getResultFrameByteCount());
        drawingBuffer.rewind();
        Log.d(TAG, "drawingBuffer capacity: " + drawingBuffer.capacity() + " buffer is direct: " + drawingBuffer.isDirect() + " remaining:" + drawingBuffer.remaining());
        drawingBitmap.copyPixelsFromBuffer(drawingBuffer);

        try {
            canvas.drawBitmap(drawingBitmap, srcRect, surfaceHolder.getSurfaceFrame(), null);
            if (facelets != null && drawing && drawingFromJava) {
                facelets = RubikDetectorUtils.rescaleResults(facelets,
                        rubikDetector.getFrameWidth(),
                        rubikDetector.getFrameHeight(),
                        surfaceHolder.getSurfaceFrame().width(),
                        surfaceHolder.getSurfaceFrame().height());
                Log.d(TAG, "drawing facelets!");

                RubikDetectorUtils.drawFaceletsAsRectangles(facelets, canvas, paint);

                ///////////getting cube facelets colors on click////////////

                RubikFacelet[][] finalFacelets = facelets;



            } else {
                Log.d(TAG, "facelets are null!");
            }
        } catch (Exception e) {
            Log.w(TAG, "Exception while rendering", e);
        } finally {
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public static String getCubeColorString(@NonNull RubikFacelet[][] result) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                switch (result[i][j].color) {
                    case RubikFacelet.Color.WHITE:
                        stringBuilder.append("W");
                        break;
                    case RubikFacelet.Color.YELLOW:
                        stringBuilder.append("Y");
                        break;
                    case RubikFacelet.Color.RED:
                        stringBuilder.append("R");
                        break;
                    case RubikFacelet.Color.BLUE:
                        stringBuilder.append("B");
                        break;
                    case RubikFacelet.Color.GREEN:
                        stringBuilder.append("G");
                        break;
                    case RubikFacelet.Color.ORANGE:
                        stringBuilder.append("O");
                        break;

                }

            }
        }
        return stringBuilder.toString();
    }

    private void allocateAndSetBuffers() {
        drawingBuffer = ByteBuffer.allocate(rubikDetector.getResultFrameByteCount());
        drawingBitmap = Bitmap.createBitmap(previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888);

        byte[] dataBuffer = ByteBuffer.allocateDirect(rubikDetector.getRequiredMemory()).array();
        currentConfigPreviewFrameByteCount = dataBuffer.length;
        camera.addCallbackBuffer(dataBuffer);
        Log.w(TAG, "Allocated buffer1:" + dataBuffer + " size:" + dataBuffer.length);

        dataBuffer = ByteBuffer.allocateDirect(rubikDetector.getRequiredMemory()).array();
        camera.addCallbackBuffer(dataBuffer);
        Log.w(TAG, "Allocated buffer2:" + dataBuffer + " size:" + dataBuffer.length);

        dataBuffer = ByteBuffer.allocateDirect(rubikDetector.getRequiredMemory()).array();
        camera.addCallbackBuffer(dataBuffer);
        Log.w(TAG, "Allocated buffer3:" + dataBuffer + " size:" + dataBuffer.length);
        camera.setPreviewCallbackWithBuffer(this);
    }

    private Camera.Size findHighResValidPreviewSize(final Camera camera) {
        int minWidthDiff = 100000;
        Camera.Size desiredWidth = camera.new Size(LiveDetectionActivity.DEFAULT_PREVIEW_WIDTH, LiveDetectionActivity.DEFAULT_PREVIEW_HEIGHT);
        for (Camera.Size size : validPreviewFormatSizes) {
            int diff = LiveDetectionActivity.DEFAULT_PREVIEW_WIDTH - size.width;

            if (Math.abs(diff) < minWidthDiff) {
                minWidthDiff = Math.abs(diff);
                desiredWidth = size;
            }
        }
        return desiredWidth;
    }

    private void initializePaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setColor(Color.DKGRAY);
    }
}

