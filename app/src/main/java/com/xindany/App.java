package com.xindany;

import android.app.Application;
import android.content.SharedPreferences;

import com.ezvizuikit.open.EZUIKit;
import com.xindany.socket.SocketClient;
import com.xindany.socket.SocketServer;
import com.xindany.tao_fen_ny.R;

import static com.xindany.tao_fen_ny.PlayActivity.APPKEY;
import static com.xindany.tao_fen_ny.PlayActivity.AccessToekn;
import static com.xindany.tao_fen_ny.PlayActivity.PLAY_URL;

/**
 * Created by chenqiao on 2018/7/26.
 */

public class App extends Application {

    private static App mApp;


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

    }

    public static App getInstance() {
        return mApp;
    }

}
