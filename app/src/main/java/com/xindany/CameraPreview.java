package com.xindany;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

/**
 * Created by chenqiao on 2019/5/3.
 * e-mail : mrjctech@gmail.com
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, View.OnClickListener {

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera){
        super(context);
        mCamera = camera;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
            setOnClickListener(this);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int high) {
        if (mSurfaceHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Log.e("Camera", "onPreviewFrame():" + data.length);

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            mCamera.stopPreview();
            mCamera.release();
            if(mCamera != null){
                mCamera = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        if (mCamera != null){
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    Log.e("onAutoFocus", "result:"+success);

                }
            });
        }

    }
}