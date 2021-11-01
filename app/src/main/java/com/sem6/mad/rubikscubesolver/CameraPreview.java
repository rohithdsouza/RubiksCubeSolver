package com.sem6.mad.rubikscubesolver;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "CameraViewFragment";
    CameraBridgeViewBase cameraBridgeViewBase;
    Mat mRGBA;
    Mat mRGBAT;

    private CameraBridgeViewBase mOpenCvCameraView;
    private TextView textView;
    private Mat mRgba;
    private final Handler handler = new Handler();
    private String[] faces;
    private int scanCount = 0;
    private int leftColor, rightColor;

    private ArrayList<DetectionBox> boxes;

    private Point[] boxLocations = {
            new Point(-1,0),new Point(-0.5,-0.5),new Point(0,-1),
            new Point(-0.5,0.5),new Point(0,0),new Point(0.5,-0.5),
            new Point(0,1),new Point(0.5,0.5),new Point(1,0)};
    private int boxLayoutDistance = 600;
    private int boxSize = 110;


     BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    cameraBridgeViewBase.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(CameraPreview.this, new String[]{Manifest.permission.CAMERA},1);
        setContentView(R.layout.activity_camera_preview);
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.camera_surface);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

      //  requestWindowFeature(1);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
     //   getWindow().setStatusBarColor(Color.TRANSPARENT);
        Log.i(TAG,"Called oncreate");

        faces = new String[6];
       // autoRefresh();
      //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if request is denied, this will return an empty array
        switch(requestCode){
            case 1:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    cameraBridgeViewBase.setCameraPermissionGranted();
                }
                else{
                    //permisiion denied
                }
                return;
            }
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat();
        mRGBAT = new Mat(height, width, CvType.CV_8UC1);
        Log.i(TAG,"Called onCameraViewStarted");

    }

    @Override
    public void onCameraViewStopped() {
        Log.i(TAG,"Called onCameraViewStopped");
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();
        mRGBAT = inputFrame.gray();

       // mRgba = inputFrame.rgba();
        Log.i(TAG,"Called onCameraFrame");
//        if(boxes == null){
//            Point tempCenter = new Point(mRgba.width()/2,mRgba.height()/2);
//            boxes = new ArrayList<>();
//            for(Point boxLocation: boxLocations){
//                boxes.add(new DetectionBox(new Point(boxLocation.x*boxLayoutDistance+tempCenter.x,boxLocation.y*boxLayoutDistance+tempCenter.y),boxSize));
//            }
//        }
//
//        processColor();
//        drawOnFrame();
        return mRGBA;
    }

    private void processColor(){
        for(DetectionBox box : boxes) {
            Mat regionRgba = mRgba.submat(box.getRect());
            Mat regionHsv = new Mat();
            Imgproc.cvtColor(regionRgba, regionHsv, Imgproc.COLOR_RGB2HSV_FULL);
            Scalar tempHsv = Core.sumElems(regionHsv);
            int pointCount = box.getRect().width * box.getRect().height;
            for (int i = 0; i < tempHsv.val.length; i++) {
                tempHsv.val[i] /= pointCount;
            }
            Log.i(TAG,"Called processColor");
            box.setColorHsv(tempHsv);
        }
    }

    private void drawOnFrame(){
        for (int i = 0; i < boxes.size(); i++) {
            DetectionBox box = boxes.get(i);

            Point textDrawPoint = new Point(box.getCenter().x-box.getSize(),box.getCenter().y+box.getSize());
            Imgproc.rectangle(mRgba, box.getTopLeftPoint(), box.getBottomRightPoint(), new Scalar(255,0,0,255), 4);
            Imgproc.putText(mRgba,(i+1)+":"+box.getColor(),textDrawPoint,1,6,new Scalar(0,0,255,255),10);
        }
        Log.i(TAG,"Called drawOnFrame");
    }

    private void autoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(boxes != null) {
//                    textView.setText("Color: " + boxes.get(0).getColorHsv().val[0] + ',' + boxes.get(0).getColorHsv().val[1] + ',' + boxes.get(0).getColorHsv().val[2]);
                    textView.setTextColor(Color.BLUE);
                }
                autoRefresh();
            }
        }, 100);
    }

    private void printFace() {
        String tempString = "";
        for (int i = 0; i < boxes.size(); i++) {
            DetectionBox box = boxes.get(i);
            tempString += box.getColor() + " ";
            if(i%3==2){
                Log.i("CubeFace", tempString);
                tempString = "";
            }
        }
    }

    private int saveFace(){
        String tempString = "";
        for (int i = 0; i < boxes.size(); i++) {
            DetectionBox box = boxes.get(i);
            tempString += box.getColor();
        }
        int index = 0;
        if(tempString.substring(4,5).equals("W")){
            index = 0;
        }else if(tempString.substring(4,5).equals("R")){
            index = 1;
        }else if(tempString.substring(4,5).equals("G")){
            index = 2;
        }else if(tempString.substring(4,5).equals("Y")){
            index = 3;
        }else if(tempString.substring(4,5).equals("O")){
            index = 4;
        }else if(tempString.substring(4,5).equals("B")){
            index = 5;
        }

        faces[index] = tempString;

        return index;
    }


    @Override
    public void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()){
//            if load success
            Log.d(TAG, "onResume: Opencv initialized");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        else{
            Log.d(TAG, "onResume: Opencv not initialized");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, baseLoaderCallback);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(cameraBridgeViewBase !=null){
            cameraBridgeViewBase.disableView();
        }
        Log.i(TAG,"Called onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase !=null){
            cameraBridgeViewBase.disableView();
        }
        Log.i(TAG,"Called onDestroy");
    }


}