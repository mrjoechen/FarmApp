package com.xindany.tao_fen_ny;

import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

/**
 * Created by chenqiao on 2018/8/12.
 */

public class FullScreenNavigationView extends NavigationView {

    private final static String TAG  = FullScreenNavigationView.class.getSimpleName();

    public FullScreenNavigationView(Context context) {
        super(context);
        initView(context);
    }

    public FullScreenNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FullScreenNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(final Context context) {
        //侦测待UI完全加载完成才允许计算宽值，否则取得值为0
        ViewTreeObserver vto = this.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                setFullScreenWidth(context);
            }
        });
    }

    public void setFullScreenWidth(Context context) {
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) this.getLayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        params.width = dm.widthPixels;
        this.setLayoutParams(params);
    }
}
