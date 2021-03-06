package com.yinyutech.xiaolerobot.ui.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.net.YTXCommunicate;
import com.yinyutech.xiaolerobot.ui.fragment.HomeFragment;
import com.yinyutech.xiaolerobot.utils.Constant;
import com.yinyutech.xiaolerobot.utils.soundbox.XiaoLeLocalSendingCommand;

/**
 * Created by yinyu-tiejiang on 17-8-30.
 */

public class MySurfaceViewHeadControler extends SurfaceView implements SurfaceHolder.Callback{

    private float screenW;        //屏幕宽度
    private float screenH;        //屏幕高度
    private Paint paint;        //定义画笔
    private float[] mControlPointCoord = {0, 0};  //(x, y)
    private float cx = 50;      //圆点默认X坐标
    private float cy = 50;      //圆点默认Y坐标
    private float radius ;    //移动小球的半径值
    //定义颜色数组
    private int colorArray[] = {Color.BLACK,Color.BLACK,Color.GREEN,Color.YELLOW, Color.RED};
    private int paintColor = colorArray[0]; //定义画笔默认颜色
    private Canvas canvas = null; //定义画布
    //    private Thread th = null;     //定义线程
    private SurfaceHolder sfh = null;
    private Resources mResources = getResources();
    private Bitmap mBitmap;
    private boolean beginDrawing = false;
    private Context mMySurfaceViewControlerHeadContext;
    private boolean isLocalNetControl = false;  //是否开启局域网的控制
    private XiaoLeLocalSendingCommand mXiaoLeLocalSendingCommand;
    private HomeFragment mHomeFragment;
    private MySurfaceViewControler mySurfaceViewControler;
    private YTXCommunicate mYTXCommunicate;


    public MySurfaceViewHeadControler(Context context){
        super(context);
//        Log.d("TIEJIANG", "MySurfaceViewControler---MySurfaceViewControler( )");
    }

    public MySurfaceViewHeadControler(Context context, AttributeSet attrs){
        super(context, attrs);
        this.mMySurfaceViewControlerHeadContext = context;
//        Log.d("TIEJIANG", "MySurfaceViewControler---MySurfaceViewControler( , )");
         /*备注1：在此处获取屏幕高、宽值为0，以为此时view还未被创建，
         * 在接口Callback的surfaceCreated方法中view才被创建
         */
        /*screenW = getWidth();
        screenH = getHeight();*/

        //注释以下三句,保留surfaceview黑框背景
        setBackgroundResource(R.drawable.hand_control_base);
        setZOrderOnTop(true);//使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//使窗口支持透明度

        //初始化画笔
        initPaint();
        sfh = getHolder();
        sfh.addCallback(this);
        mYTXCommunicate = YTXCommunicate.getYTXCommunicateInstance();
    }

    private void initPaint(){
        paint = new Paint();
        //设置消除锯齿
        paint.setAntiAlias(true);
        //设置画笔颜色
        paint.setColor(paintColor);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        Log.d("TIEJIANG", "MySurfaceViewControler---onAttachedToWindow");
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
//        Log.d("TIEJIANG", "MySurfaceViewControler---onWindowVisibilityChanged");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        Log.d("TIEJIANG", "MySurfaceViewControler---onDetachedFromWindow");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        float[] mScreenData = getRect();
        screenW = mScreenData[0];
        screenH = mScreenData[1];
        //mHomeFragment 实例　（程序开始时候，此处还不能够获得ＤeviceControlFragment实例）
        mHomeFragment = ActivityInstance.mMainActivityInstance.getHomeFragmentInstance();
        //获得SurfaceViewControler实例
        mySurfaceViewControler =  mHomeFragment.getMySurfaceViewControlerInstance();
        mXiaoLeLocalSendingCommand = XiaoLeLocalSendingCommand.getInstance();
//        mHomeFragment.changeControleModel(new ControlModelChanged() {
//            @Override
//            public void isLocalNetControl(boolean is_local_net_control) {
////                Log.d("TIEJIANG", "MySurfaceViewControler---surfaceCreated" + " is_local_net_control= "+is_local_net_control);
//
//                if (is_local_net_control){
//                    isLocalNetControl = true;
//                }else {
//                    isLocalNetControl = false;
//                }
//
//            }
//        });

        //启动绘图线程
        beginDrawing = true;
        // 避免线程
        new Thread(new DrawViewRunnable()).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        mBitmap = BitmapFactory.decodeResource(mResources, R.drawable.control_point);
//        backgroundBitmap = BitmapFactory.decodeResource(mResources, R.drawable.hand_control_base);
        radius = mBitmap.getHeight()/2;

        cx = screenW/2 - radius;
        cy = screenH/2 - radius;
        Log.d("TIEJIANG", "surfaceChanged---radius= " + radius + ", cx= " + cx + ", cy= " + cy);

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //停止绘制线程
        beginDrawing = false;
        Log.d("TIEJIANG", "surfaceView destoryed " + "beginDrawing= " + beginDrawing);
    }

    class DrawViewRunnable implements Runnable {

        @Override
        public void run() {
            while(beginDrawing){
                try{
                    myDraw();
                    Thread.sleep(200);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public float[] getRect(){
        //获取屏幕宽度 (实际为获取了所创建的surfaceview的大小,并且是"绘制区域"大小)
        //区域分为:屏幕区域/应用区域/绘制区域
        float[] coord = new float[2];
        //获取屏幕宽,高度
        coord[0] = getWidth();
        coord[1] = getHeight();
        return coord;

    }

    /**
     * return 1:forward; 2 back; 3 turn left; 4 turn right; 0 origin point
     * */
    public void directionControl(float x, float y){

        float circlePointX = screenW/2 - radius;
        float circlePointY = screenH/2 - radius;
        float equationOne = 0; //(x,y)和方程1比较的值
        float equationTwo = 0;//(x,y)和方程2比较的值
        /**
         * 构建方程
         * 方程1: y = (circlePointY)/(circlePointX) * x
         * 方程2: (y-circlePointY)/circlePointY = (x-circlePointX)/(screenW-circlePointX)
         * 方程2等价于: (-circlePointY)*x + (circlePointX-screenW+radius)*y + (screenW-radius)*circlePointY = 0
         * */
        equationOne = (circlePointY)/(circlePointX) * x - y;
        equationTwo = (-circlePointY)*x + (circlePointX-screenW+radius)*y + (screenW-radius)*circlePointY;
//        Log.d("TIEJIANG", "equationOne= " + equationOne + ", equationTwo= " + equationTwo);

        // test code begin
//        isWLANOK = true;
//        isLocalNetControl = false;
        // test code end

        String sendCommand = "";
        // 注意去掉等号部分,等号部分在原点--初始位置
        if (equationOne > 0 && equationTwo > 0){  //"抬头区域"
            sendCommand = Constant.MOBILE_TURN_HEAD_UP;
//            Log.d("TIEJIANG", "forward");
        } else if (equationOne < 0 && equationTwo < 0){  // "低头区域"
            sendCommand = Constant.MOBILE_TURN_HEAD_DOWN;
//            Log.d("TIEJIANG", "back");
        } else if(equationOne < 0 && equationTwo > 0){    //"左转区域"
            sendCommand = Constant.MOBILE_TURN_HEAD_LEFT;
//            Log.d("TIEJIANG", "turn left");
        } else if (equationOne > 0 && equationTwo < 0){    //"右转区域"
            sendCommand = Constant.MOBILE_TURN_HEAD_RIGHT;
//            Log.d("TIEJIANG", "turn right");
        }
        if (!sendCommand.equals("")){
            //组装指令－－＞发送
            if (mySurfaceViewControler.isLocalNetControl){
                mXiaoLeLocalSendingCommand.startLocalSending(sendCommand);
                Log.d("TIEJIANG", "MySurfaceViewHeadControler---directionControl local sending");
            }else {
                //先通过HomeFragment得到MySurfaceViewControler的实例，在调用其方法
                mYTXCommunicate.handleSendTextMessage(sendCommand);
                Log.d("TIEJIANG", "MySurfaceViewHeadControler---directionControl YTX sending");
            }
        }
    }

    /*备注2：切记，在自定SurfaceView中定义的myDraw方法，自定义View（继承自View的子类）中的onDraw方法
     * 完全是两码事：
     * 1）自定义View（继承自View的子类）中的onDraw方法是重写父类的onDraw方法，在调用postInvalidate后会自动回调该onDraw()方法。
     * 2）此处的myDraw方法需要手动调用，所以此处故意将方法命名为myDraw，突出为该方法是自己写的，非重写父类的方法 。
     *
     */
    //重写onDraw方法实现绘图操作
    protected void myDraw() {

        //获取canvas实例
        canvas = sfh.lockCanvas();
        try {
            //画背景
//            canvas.drawBitmap(backgroundBitmap, cx, cy, paint);

            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
            canvas.drawBitmap(mBitmap, cx, cy, paint);
//            Log.d("TIEJIANG", "THREAD ALWAYS RUN");
            directionControl(cx, cy);
            //修正圆点坐标
            revise();
            //随机设置画笔颜色
//        setPaintRandomColor();
        }catch (java.lang.NullPointerException e){
            Log.d("TIEJIANG", "NuLLPointerException");
        }
        if (canvas != null){
            //将画好的画布提交
            sfh.unlockCanvasAndPost(canvas);
        }

    }

    //修正圆点坐标
    private void revise(){
        if(cx <= radius){
            cx = radius;
        }else if(cx >= (screenW-radius)){
            cx = screenW-radius;
        }
        if(cy <= radius){
            cy = radius;
        }else if(cy >= (screenH-radius)){
            cy = screenH-radius;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下
                cx = (int) event.getX() - radius;
                cy = (int) event.getY() - radius;
                Log.d("TIEJIANG", "ACTION_DOWN---CX= " + cx + ", CY= " + cy);
                break;
            case MotionEvent.ACTION_MOVE:
                // 移动
                cx = (int) event.getX() - radius;
                cy = (int) event.getY() - radius;

//                Log.d("TIEJIANG", "CX= " + cx + ", CY= " + cy);
                break;
            case MotionEvent.ACTION_UP:
                // 抬起
                cx = (int) event.getX() - radius;
                cy = (int) event.getY() - radius;

                //小球回到原点
                cx = screenW/2 - radius;
                cy = screenH/2 - radius;
                break;
        }

        /*
         * 备注1：此处一定要将return super.onTouchEvent(event)修改为return true，原因是：
         * 1）父类的onTouchEvent(event)方法可能没有做任何处理，但是返回了false。
         * 2)一旦返回false，在该方法中再也不会收到MotionEvent.ACTION_MOVE及MotionEvent.ACTION_UP事件。
         */
        //return super.onTouchEvent(event);
        return true;
    }
}
