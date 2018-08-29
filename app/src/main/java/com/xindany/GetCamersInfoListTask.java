package com.xindany;

import android.os.AsyncTask;

import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LogUtil;
import com.xindany.util.T;

import java.util.List;

/**
 * Created by chenqiao on 2018/8/25.
 */

public class GetCamersInfoListTask extends AsyncTask<Void, Void, List<EZDeviceInfo>> {


    private int mErrorCode = 0;


    @Override
    protected List<EZDeviceInfo> doInBackground(Void... params) {

        if (!ConnectionDetector.isNetworkAvailable(App.getInstance())) {
            mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
            return null;
        }

        try {
            EZOpenSDK openSDK = App.getOpenSDK();
            if (openSDK == null){
                T.show(App.getInstance(), "请绑定摄像头");
                return null;
            }
            List<EZDeviceInfo> result = openSDK.getDeviceList(0, 20);

            return result;

        } catch (BaseException e) {
            ErrorInfo errorInfo = (ErrorInfo) e.getObject();
            mErrorCode = errorInfo.errorCode;
            LogUtil.debugLog("GetCamersInfoListTask", errorInfo.toString());

            return null;
        }
    }
}
