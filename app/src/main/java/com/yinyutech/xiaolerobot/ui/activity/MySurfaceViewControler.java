package com.yinyutech.xiaolerobot.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yinyutech.xiaolerobot.R;


public class MySurfaceViewControler extends SurfaceView implements SurfaceHolder.Callback, Runnable{
    private Paint paint;
    private Paint paint1;
    //
    private SurfaceHolder sfh;

    private int screenW, screenH;

    private int bigCX, bigCY, R1 = 100;

    private int smallCX, smallCY, r = 30;

    private Bitmap bitmap;

    private int bx, by;

    private int speed = 10;

    private int speedX, speedY;

    public MySurfaceViewControler(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);

        paint1 = new Paint();
        paint1.setColor(Color.RED);
        paint1.setAntiAlias(true);

        sfh = this.getHolder();
        sfh.addCallback(this);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        bx = 100;
        by = 100;
    }
    private void myDraw() {
        ////获取画布
        Canvas canvas = null;
        try {
            canvas = sfh.lockCanvas();

            if(canvas != null) {
                canvas.drawColor(Color.WHITE);
                paint.setAlpha(100);
                canvas.drawCircle(bigCX, bigCY, R1, paint);

                canvas.drawCircle(smallCX, smallCY, r, paint1);

                canvas.drawBitmap(bitmap, bx, by, paint1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{

            if(canvas != null) {
                sfh.unlockCanvasAndPost(canvas);
            }
        }

    }

    private void logical() {
        bx += speedX;
        by += speedY;
    }


    private boolean isOutOfCircle(double distance, int R, int r) {

        if(R - r >=  distance) {
            return false;
        } else {
            return true;
        }
    }

    private double getDistance(int sx, int sy, int bx, int by) {
        double distance = Math.sqrt(Math.pow(sx - bx, 2) + Math.pow(sy - by, 2));
        return distance;
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int tmpX = (int) event.getX();
        int tmpY = (int) event.getY();


        int action = event.getAction();
        boolean isOut = false;
        double distance = 0;
        switch(action) {
            case MotionEvent.ACTION_DOWN:

                distance = getDistance(tmpX, tmpY, bigCX, bigCY);
                isOut = isOutOfCircle(distance, R1, r);
                if(!isOut) {
                    smallCX = tmpX;
                    smallCY = tmpY;
                } else {
                    smallCX = getTempSmallX( distance, tmpX);
                    smallCY = getTempSmallY( distance, tmpY);
                }

                speedX = getSpeedX(distance, tmpX);
                speedY = getSpeedY(distance, tmpY);


                break;
            case MotionEvent.ACTION_MOVE:

                distance = getDistance(tmpX, tmpY, bigCX, bigCY);
                isOut = isOutOfCircle(distance, R1, r);
                if(!isOut) {
                    smallCX = tmpX;
                    smallCY = tmpY;
                } else {
                    smallCX = getTempSmallX( distance, tmpX);
                    smallCY = getTempSmallY( distance, tmpY);
                }
                speedX = getSpeedX(distance, tmpX);
                speedY = getSpeedY(distance, tmpY);

                break;
            case MotionEvent.ACTION_UP:
                smallCX = bigCX;
                smallCY = bigCY;
                speedX = 0;
                speedY = 0;
                break;
        }
        return true;
    }
    private int getSpeedY(double distance, int tmpY) {
        return (int) (speed * (tmpY - bigCY) / distance);
    }
    private int getSpeedX(double distance, int tmpX) {
        // TODO Auto-generated method stub
        return (int) (speed * (tmpX - bigCX) / distance);
    }
    private int getTempSmallY(double distance, int tmpY) {


        return (int) (bigCY + (R1 - r) * (tmpY - bigCY) / distance);
    }
    private int getTempSmallX(double distance, int tmpX) {
        // TODO Auto-generated method stub

        return (int) (bigCX + (R1 - r) * (tmpX - bigCX) / distance);
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        screenW = getWidth();
        screenH = getHeight();
        bigCX = R1 + 50;
        bigCY = screenH - R1 - 50;

        smallCX = bigCX;
        smallCY = bigCY;

        flag = true;
        Thread t = new Thread(this);
        t.start();

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        flag = false;
    }

    private boolean flag = false;
    @Override
    public void run() {

        while(flag) {
            myDraw();

            logical();
            try {
                Thread.sleep(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}