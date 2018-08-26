package com.xindany;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

import com.xindany.util.ActivityUtils;

/**
 * Created by chenqiao on 2018/8/25.
 */

public class LogoutTask extends AsyncTask<Void, Void, Void> {

    private Context mContext;
    private Dialog mWaitDialog;

    public LogoutTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mWaitDialog = new WaitDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        mWaitDialog.setCancelable(false);
        mWaitDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        App.getOpenSDK().logout();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        mWaitDialog.dismiss();
        ActivityUtils.goToLoginAgain();
    }
}