package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edmodo.cropper.CropImageView;
import com.xindany.OpenCvUtil;
import com.xindany.util.T;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by chenqiao on 2018/8/27.
 */

public class CropActivity extends Activity {

    private String pic;

    private HandlerThread thread;
    private Handler mThreadHandler;

    private Handler mHandler = new Handler();
    private ImageView cropImageView;
    private ImageView cropImageView1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Intent intent = getIntent();
        if (intent != null){
            pic = intent.getStringExtra("pic");
        }

        cropImageView1 = (ImageView) findViewById(R.id.CropImageView);

        try {
            cropImageView1.setImageBitmap(adjustPhotoRotation(decodeFile(pic), 90));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 当触摸时候才显示网格线
//        cropImageView1.setGuidelines(CropImageView.DEFAULT_GUIDELINES);

        findViewById(R.id.crop).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 获取裁剪成的图片
//                Bitmap croppedImage = cropImageView1.getCroppedImage();

//                cropImageView1.setImageBitmap(croppedImage);
            }
        });

        findViewById(R.id.jiaozheng).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (thread == null || !thread.isAlive()){
                    thread = new HandlerThread("jiaozheng");
                    thread.start();
                }
                mThreadHandler = new Handler(thread.getLooper());


                T.show(CropActivity.this, "正在矫正...");


                mThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        exeJiaozheng();
                    }
                });

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

//                        Glide.with(CropActivity.this).load(new File("/mnt/sdcard/out.jpg"));
                        try {
                            cropImageView1.setImageBitmap(decodeFile("/mnt/sdcard/out.jpg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);


            }
        });

    }

    private void exeJiaozheng() {
        try {

            copy(new File(pic), new File("/mnt/sdcard/in.jpg"), true);

            OpenCvUtil openCvUtil = new OpenCvUtil();
            Bitmap bitmap = decodeFile(pic);
            int w = bitmap.getWidth(), h = bitmap.getHeight();
            int[] pix = new int[w * h];
            bitmap.getPixels(pix, 0, w, 0, 0, w, h);
            int [] resultPixes=openCvUtil.jiaozheng(pix,w,h);
//            Bitmap result = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
//            result.setPixels(resultPixes, 0, w, 0, 0,w, h);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据 路径 得到 file 得到 bitmap
     * @param filePath
     * @return
     * @throws IOException
     */
    public Bitmap decodeFile(String filePath) throws IOException{
        Bitmap b = null;
        int IMAGE_MAX_SIZE = 600;

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


}
