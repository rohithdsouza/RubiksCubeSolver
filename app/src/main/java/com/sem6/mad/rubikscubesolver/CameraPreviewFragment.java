package com.sem6.mad.rubikscubesolver;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraPreviewFragment extends Fragment implements CameraBridgeViewBase.CvCameraViewListener2 {

    CameraBridgeViewBase cameraBridgeViewBase;
    private static final String TAG = "CameraViewFragment";
    Mat mRGBA;
    Mat mRGBAT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_camera_preview, container, false);
        Log.i(TAG,"Called oncreate");
        super.onCreate(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getActivity().setContentView(R.layout.fragment_camera_preview);

        cameraBridgeViewBase = (CameraBridgeViewBase) getActivity().findViewById(R.id.camera_surface);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        return rootView;
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        //initialize your view here for use view.findViewById("your view id")
//        cameraBridgeViewBase = (CameraBridgeViewBase) view.findViewById(R.id.camera_surface);
//        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
//        cameraBridgeViewBase.setCvCameraViewListener(this);
//    }



    public CameraPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        mRGBA = new Mat();
        mRGBAT = new Mat(height, width, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();
        mRGBAT = inputFrame.gray();
        return mRGBA;
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this.getActivity(), baseLoaderCallback);
       }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(cameraBridgeViewBase !=null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(cameraBridgeViewBase !=null){
            cameraBridgeViewBase.disableView();
        }
    }

    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
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

}