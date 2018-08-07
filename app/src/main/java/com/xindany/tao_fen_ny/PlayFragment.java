package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.videogo.util.LogUtil;
import com.xindany.App;
import com.xindany.Config;

import java.util.Calendar;

/**
 * 预览界面
 */
public class PlayFragment extends Fragment implements View.OnClickListener, EZUIPlayer.EZUIPlayerCallBack {
    private static final String TAG = "PlayActivity";
    public static final String APPKEY = "AppKey";
    public static final String AccessToekn = "AccessToekn";
    public static final String PLAY_URL = "play_url";
    public static final String Global_AreanDomain = "global_arean_domain";
    private EZUIPlayer mEZUIPlayer;

    private Button mBtnPlay;
    /**
     * onresume时是否恢复播放
     */
    private boolean isResumePlay = false;

    /**
     *  开发者申请的Appkey
     */
    private String appkey;
    /**
     *  授权accesstoken
     */
    private String accesstoken;
    /**
     *  播放url：ezopen协议
     */
    private String playUrl;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View inflate = inflater.inflate(R.layout.activity_play, null);
        appkey = Config.APP_KEY;
        accesstoken = Config.ACCESS_KEY;
        playUrl = Config.PLAY_URL_HD;

        mBtnPlay = (Button) inflate.findViewById(R.id.btn_play);

        //获取EZUIPlayer实例
        mEZUIPlayer = (EZUIPlayer) inflate.findViewById(R.id.player_ui);

        return inflate;
    }


    public void start(){
        //设置加载需要显示的view
        mEZUIPlayer.setLoadingView(initProgressBar());

        mBtnPlay.setOnClickListener(this);
        mBtnPlay.setText(R.string.string_stop_play);
        preparePlay();

        //界面stop时，如果在播放，那isResumePlay标志位置为true，resume时恢复播放
        if (isResumePlay) {
            isResumePlay = false;
            mBtnPlay.setText(R.string.string_stop_play);
            mEZUIPlayer.startPlay();
        }
    }

    public void stop(){
        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
        if (mEZUIPlayer.getStatus() != EZUIPlayer.STATUS_STOP) {
            isResumePlay = true;
        }
        //停止播放
        mEZUIPlayer.stopPlay();
        //释放资源
        mEZUIPlayer.releasePlayer();
    }


    /**
     * 创建加载view
     * @return
     */
    private View initProgressBar() {
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        relativeLayout.setBackgroundColor(Color.parseColor("#000000"));
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(lp);
        RelativeLayout.LayoutParams rlp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);//addRule参数对应RelativeLayout XML布局的属性
        ProgressBar mProgressBar = new ProgressBar(getActivity());
        mProgressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress));
        relativeLayout.addView(mProgressBar,rlp);
        return relativeLayout;
    }

    /**
     * 准备播放资源参数
     */
    private void preparePlay(){
        //设置debug模式，输出log信息
        EZUIKit.setDebug(true);
        EZUIKit.initWithAppKey(App.getInstance(), appkey);
        //设置授权accesstoken
        EZUIKit.setAccessToken(accesstoken);
        //设置播放资源参数
        mEZUIPlayer.setCallBack(this);
        mEZUIPlayer.setUrl(playUrl);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause + "+mEZUIPlayer.getStatus());

        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
        if (mEZUIPlayer.getStatus() != EZUIPlayer.STATUS_STOP) {
            isResumePlay = true;
        }
        //停止播放
        mEZUIPlayer.stopPlay();
        //释放资源
        mEZUIPlayer.releasePlayer();
    }

    @Override
    public void onPlaySuccess() {
        Log.d(TAG,"onPlaySuccess");
        // TODO: 2017/2/7 播放成功处理
        mBtnPlay.setText(R.string.string_pause_play);
    }

    @Override
    public void onPlayFail(EZUIError error) {
        Log.d(TAG,"onPlayFail");
        // TODO: 2017/2/21 播放失败处理
        if (error.getErrorString().equals(EZUIError.UE_ERROR_INNER_VERIFYCODE_ERROR)){

        }else if(error.getErrorString().equalsIgnoreCase(EZUIError.UE_ERROR_NOT_FOUND_RECORD_FILES)){
            // TODO: 2017/5/12
            //未发现录像文件
            Toast.makeText(getActivity(),getString(R.string.string_not_found_recordfile), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onVideoSizeChange(int width, int height) {
        // TODO: 2017/2/16 播放视频分辨率回调
        Log.d(TAG,"onVideoSizeChange  width = "+width+"   height = "+height);
    }

    @Override
    public void onPrepared() {
        Log.d(TAG,"onPrepared");
        //播放
        mEZUIPlayer.startPlay();
    }

    @Override
    public void onPlayTime(Calendar calendar) {
        Log.d(TAG,"onPlayTime");
        if (calendar != null) {
            // TODO: 2017/2/16 当前播放时间
            Log.d(TAG,"onPlayTime calendar = "+calendar.getTime().toString());
        }
    }

    @Override
    public void onPlayFinish() {
        // TODO: 2017/2/16 播放结束
        Log.d(TAG,"onPlayFinish");
    }


    @Override
    public void onClick(View view) {
        if (view == mBtnPlay){
            // TODO: 2017/2/14
            if (mEZUIPlayer.getStatus() == EZUIPlayer.STATUS_PLAY) {
                //播放状态，点击停止播放
                mBtnPlay.setText(R.string.string_start_play);
                mEZUIPlayer.stopPlay();
            } else if (mEZUIPlayer.getStatus() == EZUIPlayer.STATUS_STOP) {
                //停止状态，点击播放
                mBtnPlay.setText(R.string.string_stop_play);
                mEZUIPlayer.startPlay();
            }
        }
    }




}