package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xindany.OpenCvUtil;
import com.xindany.util.SPUtils;
import com.xindany.util.StringUtils;
import com.xindany.util.T;

/**
 * Created by chenqiao on 2018/8/7.
 */

public class LoginActivity extends Activity {

    private EditText tv_user;
    private EditText tv_pwd;
    private RadioButton rb_save_pwd;
    private Button btn_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        OpenCvUtil openCvUtil = new OpenCvUtil();
        Log.d("chenqiao", openCvUtil.getString());

        tv_user = findViewById(R.id.tv_user);
        tv_pwd = findViewById(R.id.tv_pwd);
        rb_save_pwd = findViewById(R.id.rb_save_pwd);
        btn_login = findViewById(R.id.btn_login);
        String temp_pwd = (String) SPUtils.get(LoginActivity.this, "temp_pwd", "haust");
        if (!StringUtils.isBlank(temp_pwd) ){
            rb_save_pwd.setChecked(true);
            tv_pwd.setText(temp_pwd);
        }


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isBlank(tv_user.getText().toString())){
                    T.show(LoginActivity.this, "请输入用户名！");
                    return;
                }

                if (StringUtils.isBlank(tv_pwd.getText().toString())){
                    T.show(LoginActivity.this, "请输入用户名！");
                    return;
                }

                if ("Haust".equals(tv_user.getText().toString())){
                    String pwd = (String) SPUtils.get(LoginActivity.this, "pwd", "haust");
                    if (pwd.equals(tv_pwd.getText().toString())){

                        if (rb_save_pwd.isChecked()){
                            SPUtils.put(LoginActivity.this, "temp_pwd", pwd);
                        }else {
                            SPUtils.put(LoginActivity.this, "temp_pwd", "");
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        T.show(LoginActivity.this, "密码错误！");
                    }
                }else {
                    T.show(LoginActivity.this, "用户名不存在");
                }


            }
        });

    }


}
