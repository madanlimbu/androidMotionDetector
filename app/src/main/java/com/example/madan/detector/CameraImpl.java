package com.example.madan.detector;

import android.hardware.Camera;

public class CameraImpl {
    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch(Exception e){

        }
        return c;
    }
}
