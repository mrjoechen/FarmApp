package com.xindany.socket;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenqiao on 2018/7/27.
 */

public class SocketClient {

    private static final String HOST = "10.0.0.107";//服务器地址
    private static final int PORT = 8234;//连接端口号
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private static final class Holder{
        private static final SocketClient Instance = new SocketClient();
    }


    public static SocketClient getInstance(){
        return SocketClient.Holder.Instance;
    }

    public void start(){
        new Thread(new SocketClient.SocketRunnable()).start();
    }

    /**
     * 连接服务器
     */
    private void connection() {
        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("连接服务器失败：" + ex.getMessage());
        }
    }

    public class SocketRunnable implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            connection();
            try {
                while (true) {

                    if (!socket.isClosed()) {
                        if (socket.isConnected()) {
                            if (!socket.isInputShutdown()) {
//                                String getLine = in.readLine();

                                out.print("请求连接");
                                out.flush();
                                out.close();
//                                InputStream inputStream = socket.getInputStream();
//
//                                byte[] buffer = new byte[1024];
//                                int len = 0;
//                                while( (len=inputStream.read(buffer)) != -1){
//
//                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
