package com.xindany.socket;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.xindany.App;
import com.xindany.util.SPUtils;
import com.xindany.util.StringUtils;
import com.xindany.util.T;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenqiao on 2018/7/26.
 */

public class SocketServer{

    private static final class Holder{
        private static final SocketServer Instance = new SocketServer();
    }

    private ExecutorService mExecutors = Executors.newCachedThreadPool();;

    public static SocketServer getInstance(){
        return Holder.Instance;
    }

    private Socket client;
    private ServerSocket serverSocket;

    private HandlerThread mThread = new HandlerThread("");
    private Handler mThreadHandler;


    public boolean startServer(){

        boolean result = false;

        int socketPort = (int) SPUtils.get(App.getInstance(), "socket_port", 8234);

        try {

            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            serverSocket = new ServerSocket(socketPort);


            if (!mThread.isAlive()) {
                mThread.start();
                mThreadHandler = new Handler(mThread.getLooper());
            }else {
                mThreadHandler.removeCallbacksAndMessages(null);
            }


            mThreadHandler.post(new ServerRunnable());

//            serverSocket.close();
            result = true;

        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    public boolean sendData(String data){

        if (client != null){

            try {
//                PrintWriter out = null;
//                out = new PrintWriter(new BufferedWriter(
//                        new OutputStreamWriter(client.getOutputStream())),
//                        true);
//                if (!StringUtils.isBlank(data)){
//                    ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
//                    String[] split = data.split(",");
//                    for (int i = 0; i < split.length; i++){
//                        bufferedOutputStream.write(Integer.parseInt(split[i]));
//                        Log.i("chenqiao", Integer.toHexString(Integer.parseInt(split[i])));
//
//                    }
//                    Log.i("chenqiao", bufferedOutputStream.toByteArray().toString());
//                    out.println(bufferedOutputStream.toByteArray());
//
//                }

//                ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();
//                String[] split = data.split(",");
//                for (int i = 0; i < split.length; i++){
//                    bufferedOutputStream.write(Integer.parseInt(split[i]));
//
//                }
//
//                OutputStream outputStream = client.getOutputStream();
//                outputStream.write(bufferedOutputStream.toByteArray());


                String[] split = data.split(",");
                byte[] bytes = new byte[split.length];
                for (int i = 0; i < split.length; i++){
                    bytes[i] = (byte) Integer.parseInt(split[i]);
                }

                OutputStream outputStream = client.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public boolean stopServer(){

        if (serverSocket != null){
            try {

                mThreadHandler.removeCallbacksAndMessages(null);
                if (serverSocket.isClosed()){
                    return true;
                }
                serverSocket.close();
                T.show(App.getInstance(), "服务停止！");

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return false;

    }

    public boolean restartServer(){

        if (serverSocket != null){
            try {

                mThreadHandler.removeCallbacksAndMessages(null);
                if (serverSocket.isClosed()){
                    return true;
                }
                serverSocket.close();
                startServer();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        return false;

    }


    class ServerRunnable implements Runnable {

        @Override
        public void run() {
            try {

                T.show(App.getInstance(), "服务已启动");
                Log.i("cheqniao", "服务已启动");

                while (true){
                    client = serverSocket.accept();
                    T.show(App.getInstance(), client.getInetAddress() +":" + client.getLocalPort() +" connected");
                    Log.i("chenqiao", client.getInetAddress() +":" + client.getLocalPort() +" connected");

//                    PrintWriter out = new PrintWriter(new BufferedWriter(
//                            new OutputStreamWriter(client.getOutputStream())),
//                            true);
//                    out.println(client.getInetAddress() +":" + client.getLocalPort() +" connected");
                    InputStream inputStream = client.getInputStream();
                    byte buffer[] = new byte[1024 * 4];
                    int temp = 0;
                    while ((temp = inputStream.read(buffer)) != -1) {
                        System.out.println(new String(buffer, 0, temp));
                    }

                    T.show(App.getInstance(), client.getInetAddress() +":" + client.getLocalPort() +" disconnected");
                    Log.i("cheqniao", client.getInetAddress() +":" + client.getLocalPort() +" disconnected");



                }

            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


}
