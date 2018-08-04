package com.xindany;

import android.app.Application;

import com.ezvizuikit.open.EZUIKit;
import com.xindany.socket.SocketClient;
import com.xindany.socket.SocketServer;

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
        //初始化EZUIKit
        EZUIKit.initWithAppKey(App.getInstance(),Config.APP_KEY);

        //设置授权token
        EZUIKit.setAccessToken(Config.ACCESS_KEY);

    }

    public static App getInstance() {
        return mApp;
    }

}
