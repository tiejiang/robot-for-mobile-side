package com.yinyutech.xiaolerobot.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by yinyu-tiejiang on 17-8-15.
 */

public class ControlView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

//    private ;

    public ControlView(Context context){
        super(context);


    }
    public ControlView(Context context, AttributeSet attrs){
        super(context, attrs);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void run() {

    }
}
