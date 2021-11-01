package com.sem6.mad.rubikscubesolver;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Utils {
    public static Scalar convertScalarHsv2Rgba(Scalar hsvColor){
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1,1, CvType.CV_8UC3,hsvColor);
        Imgproc.cvtColor(pointMatHsv,pointMatRgba,Imgproc.COLOR_HSV2RGB_FULL,4);
        return  new Scalar(pointMatRgba.get(0,0));
    }
}
