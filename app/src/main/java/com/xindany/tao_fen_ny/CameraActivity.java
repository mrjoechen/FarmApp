package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;

import com.xindany.CameraPreview;

public class CameraActivity extends AppCompatActivity {


    private Camera mCamera;
    private CameraPreview mPreview;

    private FrameLayout preview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
    }




    @Override
    protected void onResume() {
        super.onResume();
        if (checkCameraHardware(this)) {
            try {
                mCamera = Camera.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPreview = new CameraPreview(this, mCamera);
            preview.addView(mPreview);

        }
    }



    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

}
