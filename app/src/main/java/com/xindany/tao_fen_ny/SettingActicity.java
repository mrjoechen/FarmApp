package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xindany.App;
import com.xindany.socket.SocketServer;
import com.xindany.util.SPUtils;
import com.xindany.util.StringUtils;
import com.xindany.util.T;

/**
 * Created by chenqiao on 2018/7/29.
 */

public class SettingActicity extends Activity implements View.OnClickListener{

    private EditText edPort;
    private int socket_port;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edPort = findViewById(R.id.et_port);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        edPort.setInputType( InputType.TYPE_CLASS_NUMBER);


        socket_port = (int) SPUtils.get(App.getInstance(), "socket_port", 8234);
        edPort.setText(socket_port +"");


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_save:
                String text = edPort.getText().toString();
                if (!StringUtils.isBlank(text) && Integer.parseInt(text) > 0 && Integer.parseInt(text) < 9999){
                    if (!text.equals(socket_port+"")){
                        SPUtils.put(App.getInstance(), "socket_port", Integer.parseInt(text));
                        SocketServer.getInstance().startServer();
                        T.show(this, "保存成功");

                    }
                }else {
                    T.show(this, "输入不合法");
                }

                break;
            default:
                break;
        }

    }
}
