/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

/* Header for class com_xindany_OpenCvUtil */

/*
 * Class:     com_xindany_OpenCvUtil
 * Method:    jiaozheng
 * Signature: ([III)[I
 */


#ifndef _Included_com_xindany_OpenCvUtil
#define _Included_com_xindany_OpenCvUtil
#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jintArray JNICALL Java_com_xindany_OpenCvUtil_jiaozheng
  (JNIEnv *env, jobject obj, jintArray buf, int w, int h){

Mat image = imread("/mnt/sdcard/in.jpg", 1);
    jint *cbuf;
      cbuf = env->GetIntArrayElements(buf, JNI_FALSE );
      if (cbuf == NULL) {
          return 0;
      }

      Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);



    Size image_size = image.size();
    float intrinsic[3][3] = { 8.347918013266893e+02, 0, 6.367127655763162e+02,
    0, 8.292307453411623e+02, 3.603392433999688e+02,
    0, 0, 1 };
    float distortion[1][5] = { -0.318861538489663, 0.090963121959472, 0, 0, 0 };
    Mat intrinsic_matrix = Mat(3, 3, CV_32FC1, intrinsic);
    Mat distortion_coeffs = Mat(1, 5, CV_32FC1, distortion);
    Mat R = Mat::eye(3, 3, CV_32F);	Mat mapx = Mat(image_size, CV_32FC1);
    Mat mapy = Mat(image_size, CV_32FC1);
    initUndistortRectifyMap(intrinsic_matrix, distortion_coeffs, R, getOptimalNewCameraMatrix(intrinsic_matrix, distortion_coeffs, image_size, 1, image_size, 0), image_size, CV_32FC1, mapx, mapy);
    Mat t = image.clone();
    remap(image, t, mapx, mapy, INTER_LINEAR);

    jint* ptr = (jint*)t.ptr(0);

    imwrite("/mnt/sdcard/out.jpg", t);

      int size = w * h;
      jintArray result = env->NewIntArray(size);
      env->SetIntArrayRegion(result, 0, size, ptr);
      env->ReleaseIntArrayElements(buf, cbuf, 0);
      return result;


/*

        int size = w * h;
      	jint * pinPixels = env->GetIntArrayElements(buf, 0);

      	Mat in(h, w, CV_8UC4, (unsigned char*) pinPixels);
        Mat out(h, w, CV_8UC4, Scalar(0, 0, 255));

      	double cam[] = {w, 0, w / 2, 0, h, h / 2, 0, 0, 1 };
      	double distort[] = { 0.1, 0.35, 0.0, 0.0, 0.01 };


      	Mat camMat = Mat(3, 3, CV_64FC1, cam);
      	Mat disMat = Mat(5, 1, CV_64FC1, distort);
      	undistort(in, out, camMat, disMat);

        jint* ptr = (jint*)out.ptr(0);

        jintArray result = env->NewIntArray(size);

        env->SetIntArrayRegion(result, 0, size, ptr);

      	env->ReleaseIntArrayElements(buf, pinPixels, 0);

      return result;
      */


  }

  JNIEXPORT jintArray JNICALL Java_com_xindany_OpenCvUtil_grey
    (JNIEnv *env, jobject obj, jintArray buf, int w, int h){

  jint *cbuf;
        cbuf = env->GetIntArrayElements(buf, JNI_FALSE );
        if (cbuf == NULL) {
            return 0;
        }

        cv::Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);

        uchar* ptr = imgData.ptr(0);
        for(int i = 0; i < w*h; i ++){
            //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
            //对于一个int四字节，其彩色值存储方式为：BGRA
            int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
            ptr[4*i+1] = grayScale;
            ptr[4*i+2] = grayScale;
            ptr[4*i+0] = grayScale;
        }

        int size = w * h;
        jintArray result = env->NewIntArray(size);
        env->SetIntArrayRegion(result, 0, size, cbuf);
        env->ReleaseIntArrayElements(buf, cbuf, 0);
        return result;
    }

/*
 * Class:     com_xindany_OpenCvUtil
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_xindany_OpenCvUtil_getString
  (JNIEnv *env, jobject obj){
       return env->NewStringUTF((char *)"This just a test for Android Studio NDK JNI developer!");
  }


#ifdef __cplusplus
}
#endif
#endif