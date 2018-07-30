package com.xindany;

import android.app.Application;

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

    }

    public static App getInstance() {
        return mApp;
    }

}
