package com.xindany.tao_fen_ny;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.videogo.exception.InnerException;
import com.videogo.openapi.EZConstants;
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
import com.xindany.Config;
import com.xindany.GetCamersInfoListTask;
import com.xindany.LogoutTask;
import com.xindany.OpenCvUtil;
import com.xindany.socket.SocketServer;
import com.xindany.tao_fen_ny.scan.main.CaptureActivity;
import com.xindany.util.DataManager;
import com.xindany.util.EZUtils;
import com.xindany.util.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by chenqiao on 2018/8/25.
 */

public class EZPlayActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback, Handler.Callback{


    private SurfaceView mRealPlaySv = null;
    private SurfaceHolder mRealPlaySh = null;
    private EZDeviceInfo mDeviceInfo;
    private EZCameraInfo mCameraInfo;
    private EZPlayer mEZPlayer = null;
    private Button mBtnCapture;
    private LoadingTextView mRealPlayLoadingRl;
    private ImageView image;
    private Uri imageUri;
    private Bitmap bitmap1;
    private Bitmap bitmap2;

    private HandlerThread thread;
    private Handler mThreadHandler;

    private Handler mHandler = new Handler(this);
    private String path;
    private Bitmap result;
    private Button mBtnSave;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ezplay);


        initView();

        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startRealPlay();
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

        mBtnSave = findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(this);

        mRealPlayLoadingRl = (LoadingTextView) findViewById(R.id.realplay_loading);

        image = (ImageView) findViewById(R.id.iv);

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

        setRealPlayLoadingUI(0);

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

            mEZPlayer.setHandler(mHandler);
            mEZPlayer.setSurfaceHold(mRealPlaySh);
            mEZPlayer.startRealPlay();
        }

    }

    private void setRealPlayLoadingUI(int i) {

        mRealPlayLoadingRl.setVisibility(View.VISIBLE);
        mRealPlayLoadingRl.setText(i+"");
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

            case R.id.btn_save:
                onJiaozhengBtnClick();
                break;
            default:
                break;
        }

    }

    private void onJiaozhengBtnClick() {


        if (result == null){
            T.show(EZPlayActivity.this, "请先拍照1");
            return;
        }

        if (thread == null || !thread.isAlive()){
            thread = new HandlerThread("jiaozheng");
            thread.start();
        }


        T.show(EZPlayActivity.this, "正在保存...");


        if (mThreadHandler == null)
        mThreadHandler = new Handler(thread.getLooper());
        mThreadHandler.post(new Runnable() {
            @Override
            public void run() {

                if (result != null){
                    saveBitmap(EZPlayActivity.this, result);
                }

            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (result != null){
                    T.show(EZPlayActivity.this, "保存成功");
                    finish();

                }else {
                    T.show(EZPlayActivity.this, "请先拍照");

                }

//                        Glide.with(CropActivity.this).load(new File("/mnt/sdcard/out.jpg"));

            }
        }, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();

        SocketServer.getInstance().setEzPlayActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null && thread.isAlive()){
            thread.quit();
        }

        if (mThreadHandler != null){
            mThreadHandler.removeCallbacksAndMessages(null);
        }

        SocketServer.getInstance().setEzPlayActivity(null);

    }

    public void onCapturePicBtnClick() {

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


            if (thread == null || !thread.isAlive()){
                thread = new HandlerThread("jiaozheng");
                thread.start();
            }
            mThreadHandler = new Handler(thread.getLooper());

            T.show(EZPlayActivity.this, "请稍等...");

            mThreadHandler.post(new Runnable() {
                @Override
                public void run() {

                    Bitmap bmp = mEZPlayer.capturePicture();
                    if (bmp != null) {
                        try {

                            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
                            java.util.Date date = new java.util.Date();
                            path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/"
                                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";

                            if (TextUtils.isEmpty(path)) {
                                bmp.recycle();
                                bmp = null;
                                return;
                            }
                            EZUtils.saveCapturePictrue(path, bmp);
//                            copy(new File(path), new File("/mnt/sdcard/in.jpg"), true);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    T.show(EZPlayActivity.this, "正在拍照");

                                }
                            });

//                            mHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    image.setImageBitmap(result);
//
//                                }
//                            }, 4000);

                            Thread.sleep(3000);

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    T.show(EZPlayActivity.this, "正在矫正");

                                }
                            });

//                            MediaScanner mMediaScanner = new MediaScanner(EZPlayActivity.this);
//                            mMediaScanner.scanFile(path, "jpg");


                            OpenCvUtil openCvUtil = new OpenCvUtil();
                            Bitmap bitmap = decodeFile(path);
                            int w = bitmap.getWidth(), h = bitmap.getHeight();
                            int[] pix = new int[w * h];
                            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
                            int [] resultPixes=openCvUtil.jiaozheng(pix,w,h);

                            Bitmap resultBmp1 = decodeFile("/mnt/sdcard/out.jpg");
                            Bitmap result1 = adjustPhotoRotation(resultBmp1, 90);

                            int width = result1.getWidth();
                            int height = result1.getHeight();

//                            int x = width / 10;
//                            int y = height / 15;
//                            int width1 = width / 10 * 6;
//                            int height1 = height / 15 * 11;
                            result = Bitmap.createBitmap(result1, Config.X, Config.Y, Config.W, Config.H);
//                            result = Bitmap.createBitmap(bitmap, 90, 98, 540, 1080);

                            if (result != null){
                                saveBitmap(EZPlayActivity.this, result);
                            }

                            if ( result1 != null) {
                                result1.recycle();
                                result1 = null;
                            }

                            if (resultBmp1 != null) {
                                resultBmp1.recycle();
                                resultBmp1 = null;
                            }

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if (result != null){
                                        T.show(EZPlayActivity.this, "保存成功");
                                        finish();
                                    }else {
                                        T.show(EZPlayActivity.this, "请先拍照");

                                    }


//                        Glide.with(CropActivity.this).load(new File("/mnt/sdcard/out.jpg"));

                                }
                            }, 1000);

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
////                                    Toast.makeText(EZPlayActivity.this, "已经保存至相册"+ path, Toast.LENGTH_SHORT).show();
//
////                                    Intent intent = new Intent(EZPlayActivity.this, CropActivity.class);
////                                    intent.putExtra("pic", path);
////                                    startActivity(intent);
//
//                                    File outputImage = new File(path);
//
//                                    imageUri = Uri.fromFile(outputImage);
//                                    Intent intent = new Intent(Intent.ACTION_PICK,imageUri);
//                                    //此处调用了图片选择器
//                                    //如果直接写intent.setDataAndType("image/*");
//                                    //调用的是系统图库
//                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                    startActivityForResult(intent, 1);
//                                    finish();
//                                }
//                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            T.show(EZPlayActivity.this, "Error !");
                        } finally {
                            if (bmp != null) {
                                bmp.recycle();
                                bmp = null;
                            }

                            return;
                        }
                    }
                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
        }

        if (result != null) {
            result.recycle();
            result = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mEZPlayer != null) {
            mEZPlayer.release();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    //此处启动裁剪程序
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 2);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    try {
                        //将output_image.jpg对象解析成Bitmap对象，然后设置到ImageView中显示出来
                        bitmap1 = BitmapFactory.decodeStream(getContentResolver()
                                .openInputStream(imageUri));
                        image.setImageBitmap(bitmap1);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 复制文件
     *
     * @param src
     * @param dst
     * @param overwrite 若目标文件存在，是否覆盖
     * @return 复制成功，或无须复制
     */
    public boolean copy(@NonNull File src, @NonNull File dst, boolean overwrite) {
        if (!src.exists()) { // short cut
            return false;
        }
        File parent = dst.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            return false;
        }

        if (dst.exists()) {
            if (!overwrite) {
                return true;
            } else {
                delete(dst);
            }
        }

        File tmp = new File(dst.getAbsolutePath() + ".tmp");
        if (src.isDirectory()) {
            if (tmp.exists() || tmp.mkdirs()) {
                File[] files = src.listFiles();
                for (File subFile:
                        files) {
                    File subDst = new File(tmp, subFile.getName());
                    if(!copy(subFile, subDst, overwrite)) {
                        return false;
                    }
                }
                return tmp.renameTo(dst);
            }
            return false;
        }

        byte[] buffer = new byte[64 * 1024];
        boolean success;
        int read;
        try (InputStream is = new FileInputStream(src); OutputStream os = new FileOutputStream(tmp)) {
            while ((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }

        if (!success) {
            tmp.delete();
        } else {
            tmp.renameTo(dst);
        }

        return success;
    }

    /**
     * 删除某个文件或目录（递归删除）
     *
     * @param file
     * @return
     */
    public boolean delete(File file) {
        if (!file.exists()) {
            return true;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile :
                    files) {
                if (!delete(subFile)) {
                    return false;
                }
            }
            return file.delete();
        } else {
            return file.delete();
        }
    }

    Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {

        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bitmap.getHeight();
            targetY = 0;
        } else {
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }


        final float[] values = new float[9];
        matrix.getValues(values);


        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];


        matrix.postTranslate(targetX - x1, targetY - y1);


        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);


        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);


        return canvasBitmap;
    }

    /**
     * 根据 路径 得到 file 得到 bitmap
     * @param filePath
     * @return
     * @throws IOException
     */
    public Bitmap decodeFile(String filePath) throws IOException{
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 1280;

        File f = new File(filePath);
        if (f == null){
            return null;
        }
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();
        return b;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (this.isFinishing()) {
            return false;
        }

        switch (msg.what) {
            case EZConstants.EZRealPlayConstants.MSG_GET_CAMERA_INFO_SUCCESS:
                setRealPlayLoadingUI(20);
                break;
            case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_START:
                setRealPlayLoadingUI(40);
                break;
            case EZConstants.EZRealPlayConstants.MSG_REALPLAY_CONNECTION_START:
                setRealPlayLoadingUI(60);
                break;
            case EZConstants.EZRealPlayConstants.MSG_REALPLAY_CONNECTION_SUCCESS:
                setRealPlayLoadingUI(80);
                break;
            case EZConstants.EZRealPlayConstants.MSG_REALPLAY_PLAY_SUCCESS:
                setRealPlayLoadingUI(100);
                mRealPlayLoadingRl.setVisibility(View.GONE);

                break;
            default:
                break;
        }

        return false;
    }

    public String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath = Environment.getExternalStorageDirectory()+"/";
        File filePic;
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            savePath = SD_PATH;
//        } else {
//            savePath = context.getApplicationContext().getFilesDir()
//                    .getAbsolutePath()
//                    + IN_PATH;
//        }
        try {
            filePic = new File(savePath  + "frambg.jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
}
