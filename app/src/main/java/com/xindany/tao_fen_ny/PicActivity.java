package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.xindany.OpenCvUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by chenqiao on 2018/8/26.
 */

public class PicActivity extends Activity {

    private ImageView imageView1;
    private ImageView imageView2;

    Bitmap srcBitmap;
    Bitmap grayBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        OpenCvUtil openCvUtil = new OpenCvUtil();

        imageView1 = findViewById(R.id.img1);
        imageView2 = findViewById(R.id.img2);
        Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
                R.drawable.pic_demo)).getBitmap();
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int [] resultPixes=openCvUtil.jiaozheng(pix,w,h);
        Bitmap result = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
        result.setPixels(resultPixes, 0, w, 0, 0,w, h);

        imageView2.setImageBitmap(result);
        imageView1.setImageBitmap(bitmap);


    }

    public void procSrc2Gray(){
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pic_demo);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i("procSrc2Gray", "procSrc2Gray sucess...");
    }
}
