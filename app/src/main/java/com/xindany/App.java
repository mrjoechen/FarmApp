package com.xindany;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.ezvizuikit.open.EZUIKit;
import com.videogo.openapi.EZOpenSDK;
import com.xindany.socket.SocketClient;
import com.xindany.socket.SocketServer;
import com.xindany.tao_fen_ny.R;

import static com.xindany.tao_fen_ny.PlayActivity.APPKEY;
import static com.xindany.tao_fen_ny.PlayActivity.AccessToekn;
import static com.xindany.tao_fen_ny.PlayActivity.PLAY_URL;

/**
 * Created by chenqiao on 2018/7/26.
 */

public class App extends MultiDexApplication {

    private static App mApp;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

        SocketServer.getInstance().startServer();
//       SocketClient.getInstance().start();


        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        Config.APP_KEY = sharedPreferences.getString(APPKEY,"");
        Config.ACCESS_KEY = sharedPreferences.getString(AccessToekn,"");
        Config.PLAY_URL_HD = sharedPreferences.getString(PLAY_URL,"");

        //初始化EZUIKit
        EZUIKit.initWithAppKey(App.getInstance(),Config.APP_KEY);

        //设置授权token
        EZUIKit.setAccessToken(Config.ACCESS_KEY);

        initSDK();

    }

    public static App getInstance() {
        return mApp;
    }


    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }


    private void initSDK() {
        {
            /**
             * sdk日志开关，正式发布需要去掉
             */
            EZOpenSDK.showSDKLog(true);

            /**
             * 设置是否支持P2P取流,详见api
             */
            EZOpenSDK.enableP2P(true);

            /**
             * APP_KEY请替换成自己申请的
             */
            EZOpenSDK.initLib(this, Config.APP_KEY);
        }
    }

}
