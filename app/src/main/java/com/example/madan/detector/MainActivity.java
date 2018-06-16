package com.example.madan.detector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DetectorAPP";
    private Camera mCamera;
    private CameraPreview mPreview;
    private Button captureButton;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            // instance of Camera
            mCamera = getCameraInstance();
            if(mCamera == null){
                Log.d(TAG, "mCamera is null");
            }
            //create Preview view and set it as content of activity
            mPreview = new CameraPreview(this, mCamera);
            if(mPreview == null){
                Log.d(TAG, "mPreview is null");
            }

            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            if(preview == null){
                Log.d(TAG, "preview is null");
            }
            preview.addView(mPreview);

            setButtonListener();
        }


    }

    //create file Uri for saving image/video
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //create file for saving image/video
    private File getOutputMediaFile(int type){
       // File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
      //          Environment.DIRECTORY_PICTURES), "MyCameraApp");
        File mediaStorageDir = null;
        try {
          mediaStorageDir = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
             // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        //finally create media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath()+File.separator+
            "IMG_"+timeStamp+".jpg");
        }else if(type == MEDIA_TYPE_VIDEO){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+timeStamp+".mp4");
        }else{
            return null;
        }

        return mediaFile;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if(pictureFile == null){
                Log.d(TAG, "Error creating media file ");
                return;
            }
            try{
                System.out.println(pictureFile.toString());
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();

            }catch (FileNotFoundException e){
                Log.d(TAG, "File not found");
            }catch (IOException e){
                Log.d(TAG, "Error accessing file");
            }
        }
    };

    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch(Exception e){
            Log.d(TAG, "camera instance not created");
        }
        return c;
    }

    public void setButtonListener(){
        captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //get an image from camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
    }

    //Camera shared resouce should be freeed on pause or close for other or same app to reuse camera in future
    @Override
    protected void onPause(){
        super.onPause();
     /*   if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }*/
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

}
