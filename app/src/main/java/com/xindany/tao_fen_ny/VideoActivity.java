package com.xindany.tao_fen_ny;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ezvizuikit.open.EZUIError;
import com.ezvizuikit.open.EZUIKit;
import com.ezvizuikit.open.EZUIPlayer;
import com.xindany.App;
import com.xindany.Config;

import java.util.Calendar;

/**
 * Created by chenqiao on 2018/8/3.
 */

public class VideoActivity extends Activity{


    private EZUIPlayer mPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initView();

    }


    private void initView(){

        //获取EZUIPlayer实例
        mPlayer = (EZUIPlayer) findViewById(R.id.player_ui);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //设置播放回调callback
        mPlayer.setCallBack(new EZUIPlayer.EZUIPlayerCallBack() {
            @Override
            public void onPlaySuccess() {

            }

            @Override
            public void onPlayFail(EZUIError ezuiError) {

            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onPrepared() {
                mPlayer.startPlay();
            }

            @Override
            public void onPlayTime(Calendar calendar) {

            }

            @Override
            public void onPlayFinish() {

            }
        });

        //设置播放参数
        mPlayer.setUrl(Config.PLAY_URL_HD);
    }

    @Override
    protected void onStop() {
        super.onStop();

        //停止播放
        mPlayer.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //释放资源
        mPlayer.releasePlayer();
    }

}
