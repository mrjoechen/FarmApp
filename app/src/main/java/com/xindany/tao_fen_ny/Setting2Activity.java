package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.videogo.openapi.EZOpenSDK;
import com.xindany.App;
import com.xindany.Config;
import com.xindany.socket.SocketServer;
import com.xindany.tao_fen_ny.scan.main.CaptureActivity;
import com.xindany.util.SPUtils;
import com.xindany.util.StringUtils;
import com.xindany.util.T;

import org.json.JSONArray;
import org.json.JSONException;

import static com.xindany.tao_fen_ny.PlayActivity.APPKEY;
import static com.xindany.tao_fen_ny.PlayActivity.AccessToekn;
import static com.xindany.tao_fen_ny.PlayActivity.PLAY_URL;


public class Setting2Activity extends Activity implements View.OnClickListener {


    private EditText edPort;
    private int socket_port;
    private Button btnSave;


    /**
     * 二维码扫描按钮
     */
    private Button mButtonCode;

    /**
     * 清除播放缓存参数按钮
     */
    private Button mButtonClear;

    /**
     * 开发者申请的Appkey
     */
    private String mAppKey;

    /**
     * 授权accesstoken
     */
    private String mAccessToken;

    /**
     * 播放url：ezopen协议
     */
    private String mUrl;


    private EditText mAppkeyEditText;

    private EditText mAccessTokenEditText;

    private EditText mUrlEditText;


    private TextView mTextViewVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mButtonCode = (Button) findViewById(R.id.btn_code);
        mButtonClear = (Button) findViewById(R.id.btn_clear_cache);
        mAppkeyEditText = (EditText) findViewById(R.id.edit_appkey);
        mAccessTokenEditText = (EditText) findViewById(R.id.edit_accesstoken);
        mUrlEditText = (EditText) findViewById(R.id.edit_url);
        mTextViewVersion = (TextView) findViewById(R.id.text_version);
        mButtonCode.setOnClickListener(this);
        mButtonClear.setOnClickListener(this);
        mTextViewVersion.setText(EZUIKit.EZUIKit_Version+" (SDK "+ EZOpenSDK.getVersion()+")");


        edPort = findViewById(R.id.et_port);
        btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);
        edPort.setInputType( InputType.TYPE_CLASS_NUMBER);


        socket_port = (int) SPUtils.get(App.getInstance(), "socket_port", 8234);
        edPort.setText(socket_port +"");


        getDefaultParams();
    }

    int position = 1;
    @Override
    public void onClick(View view) {
        if (view == mButtonCode) {

           if (ContextCompat.checkSelfPermission(Setting2Activity.this,
                   android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
               ActivityCompat.requestPermissions(Setting2Activity.this,
                       new String[]{android.Manifest.permission.CAMERA},100);
           }else {
               //跳转到二维码扫描页面扫描二维码获取预览所需参数appkey、accesstoken、url
               Intent intent = new Intent(this, CaptureActivity.class);
               startActivityForResult(intent,200);
           }

        }else if(view  == mButtonClear){
            //弹出清除数据确认框
            showClearDialog();
        }else if (view == btnSave){
            String text = edPort.getText().toString();
            if (!StringUtils.isBlank(text) && Integer.parseInt(text) > 0 && Integer.parseInt(text) < 9999){
                if (!text.equals(socket_port+"")){
                    SPUtils.put(App.getInstance(), "socket_port", Integer.parseInt(text));
                    SocketServer.getInstance().startServer();
                }

                T.show(this, "保存成功");
            }else {
                T.show(this, "输入不合法");
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode){
            case 100:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //这里已经获取到了摄像头的权限，想干嘛干嘛了可以
                    //跳转到二维码扫描页面扫描二维码获取预览所需参数appkey、accesstoken、url
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent,200);
                }else {
                    //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    T.show(Setting2Activity.this,"请手动打开相机权限");
                }
                break;
            default:
                break;
        }

    }

    /**
     * 清除缓存弹框
     */
    private void showClearDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
        exitDialog.setMessage(R.string.string_btn_clear_cache_sure);
        exitDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearDefaultParams();
            }
        });
        exitDialog.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exitDialog.show();
    }

    //二维码扫描返回值获取
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == 200){
                String appkey = data.getStringExtra(APPKEY);
                String accesstoken = data.getStringExtra(AccessToekn);
                String playUrl = data.getStringExtra(PLAY_URL);
                if (!TextUtils.isEmpty(appkey)){
                    mAppKey = appkey;
                    mAppkeyEditText.setText(appkey);
                }
                if (!TextUtils.isEmpty(accesstoken)){
                    mAccessToken = accesstoken;
                    mAccessTokenEditText.setText(accesstoken);
                }
                if (!TextUtils.isEmpty(playUrl)){
                    try {
                        JSONArray jsonArray = new JSONArray(playUrl);
                        StringBuffer displayUrl = new StringBuffer();
                        for (int i =0;i<jsonArray.length();i++){
                            if (i != 0){
                                displayUrl.append(",");
                            }
                            displayUrl.append(jsonArray.getString(i));
                        }
                        mUrl = displayUrl.toString();
                        mUrlEditText.setText(mUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mUrl = playUrl;
                        mUrlEditText.setText(mUrl);
                    }
                }
                saveDefaultParams();
            }
        }
    }

    /**
     * 获取缓存播放参数
     */
    private void getDefaultParams(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        mAppKey = sharedPreferences.getString(APPKEY,"");
        mAccessToken = sharedPreferences.getString(AccessToekn,"");
        mUrl = sharedPreferences.getString(PLAY_URL,"");
        mAppkeyEditText.setText(mAppKey);
        mAccessTokenEditText.setText(mAccessToken);
        mUrlEditText.setText(mUrl);
    }

    /**
     * 缓存播放参数
     */
    private void saveDefaultParams(){
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APPKEY,mAppKey);
        editor.putString(AccessToekn,mAccessToken);
        editor.putString(PLAY_URL,mUrl);
        editor.commit();

        Config.APP_KEY = mAppKey;
        Config.ACCESS_KEY = mAccessToken;
        Config.PLAY_URL_HD = mUrl;

    }

    /**
     * 清除播放参数缓存
     */
    private void clearDefaultParams(){
        mAppKey = "";
        mAccessToken = "";
        mUrl = "";
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(APPKEY,mAppKey);
        editor.putString(AccessToekn,mAccessToken);
        editor.putString(PLAY_URL,mUrl);
        editor.commit();
        mAppkeyEditText.setText(mAppKey);
        mAccessTokenEditText.setText(mAccessToken);
        mUrlEditText.setText(mUrl);
    }
}
