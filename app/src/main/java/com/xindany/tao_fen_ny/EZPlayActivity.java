package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.videogo.exception.InnerException;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.realplay.RealPlayStatus;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.videogo.util.MediaScanner;
import com.videogo.util.SDCardUtil;
import com.videogo.util.Utils;
import com.xindany.App;
import com.xindany.GetCamersInfoListTask;
import com.xindany.LogoutTask;
import com.xindany.tao_fen_ny.scan.main.CaptureActivity;
import com.xindany.util.DataManager;
import com.xindany.util.EZUtils;
import com.xindany.util.T;

import java.util.List;

/**
 * Created by chenqiao on 2018/8/25.
 */

public class EZPlayActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback{


    private SurfaceView mRealPlaySv = null;
    private SurfaceHolder mRealPlaySh = null;
    private EZDeviceInfo mDeviceInfo;
    private EZCameraInfo mCameraInfo;
    private EZPlayer mEZPlayer = null;
    private Button mBtnCapture;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ezplay);


        initView();

        initData();
    }

    private void initData() {

        GetCamersInfoListTask getCamersInfoListTask = new GetCamersInfoListTask(){
            @Override
            protected void onPostExecute(List<EZDeviceInfo> ezDeviceInfos) {
                super.onPostExecute(ezDeviceInfos);

                if (ezDeviceInfos != null && ezDeviceInfos.size() > 0){
                    mDeviceInfo = ezDeviceInfos.get(0);

                    if (mDeviceInfo != null){
                        mCameraInfo = EZUtils.getCameraInfoFromDevice(mDeviceInfo, 0);

                        startRealPlay();
                    }
                }
            }
        };

        getCamersInfoListTask.execute();




    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mRealPlaySv = (SurfaceView) findViewById(R.id.realplay_sv);

        mRealPlaySh = mRealPlaySv.getHolder();
        mRealPlaySh.addCallback(this);

        mBtnCapture = findViewById(R.id.btn_capture);
        mBtnCapture.setOnClickListener(this);


    }


    /**
     * 开始播放
     *
     * @see
     * @since V2.0
     */
    private void startRealPlay() {
        // 增加手机客户端操作信息记录
        LogUtil.debugLog("startRealPlay", "startRealPlay");


        // 检查网络是否可用
        if (!ConnectionDetector.isNetworkAvailable(this)) {
            // 提示没有连接网络
            T.show(EZPlayActivity.this, "网络未连接！");
            return;
        }

        setRealPlayLoadingUI();

        if (mCameraInfo != null) {
            if (mEZPlayer == null) {
                mEZPlayer = App.getOpenSDK().createPlayer(mCameraInfo.getDeviceSerial(), mCameraInfo.getCameraNo());
            }

            if (mEZPlayer == null)
                return;
            if (mDeviceInfo == null) {
                return;
            }
            if (mDeviceInfo.getIsEncrypt() == 1) {
                mEZPlayer.setPlayVerifyCode(DataManager.getInstance().getDeviceSerialVerifyCode(mCameraInfo.getDeviceSerial()));
            }

//            mEZPlayer.setHandler(mHandler);
            mEZPlayer.setSurfaceHold(mRealPlaySh);
            mEZPlayer.startRealPlay();
        }

    }

    private void setRealPlayLoadingUI() {
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(holder);
        }
        mRealPlaySh = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(null);
        }
        mRealPlaySh = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_capture:
                onCapturePicBtnClick();
                break;
            default:
                break;
        }

    }

    private void onCapturePicBtnClick() {

        if (!SDCardUtil.isSDCardUseable()) {
            // 提示SD卡不可用
            Utils.showToast(EZPlayActivity.this, "sd卡不可用");
            return;
        }

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            Utils.showToast(EZPlayActivity.this, "内存不足");
            return;
        }

        if (mEZPlayer != null) {
//            updateCaptureUI();

            Thread thr = new Thread() {
                @Override
                public void run() {
                    Bitmap bmp = mEZPlayer.capturePicture();
                    if (bmp != null) {
                        try {

                            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
                            java.util.Date date = new java.util.Date();
                            final String path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/CapturePicture/" + String.format("%tY", date)
                                    + String.format("%tm", date) + String.format("%td", date) + "/"
                                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";

                            if (TextUtils.isEmpty(path)) {
                                bmp.recycle();
                                bmp = null;
                                return;
                            }
                            EZUtils.saveCapturePictrue(path, bmp);


                            MediaScanner mMediaScanner = new MediaScanner(EZPlayActivity.this);
                            mMediaScanner.scanFile(path, "jpg");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EZPlayActivity.this, "已经保存至相册"+path, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (InnerException e) {
                            e.printStackTrace();
                        } finally {
                            if (bmp != null) {
                                bmp.recycle();
                                bmp = null;
                                return;
                            }
                        }
                    }
                    super.run();
                }
            };
            thr.start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
