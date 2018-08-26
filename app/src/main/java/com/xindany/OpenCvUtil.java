package com.xindany;

/**
 * Created by chenqiao on 2018/8/26.
 */

public class OpenCvUtil {

    static {
        System.loadLibrary("OpenCvUtil");//导入生成的链接库文件
    }

    public native int[] jiaozheng(int[] buf, int w, int h);

    public native String getString();


}
