package com.yinyutech.xiaolerobot.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.common.CCPAppManager;
import com.yinyutech.xiaolerobot.entrance.ControlModelChanged;
import com.yinyutech.xiaolerobot.entrance.ImageDownloadInterface;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.net.HttpConnect;
import com.yinyutech.xiaolerobot.ui.activity.MySurfaceViewControler;
import com.yinyutech.xiaolerobot.ui.activity.MySurfaceViewHeadControler;
import com.yinyutech.xiaolerobot.ui.activity.SplashActivity;
import com.yinyutech.xiaolerobot.utils.Constant;
import com.yinyutech.xiaolerobot.utils.FileUtil;
import com.yuntongxun.ecsdk.ECVoIPCallManager;


/**
 * author　tiejiang
 * 20170810
 * */
public class HomeFragment extends BaseFragment{

    private static  final  String TAG="HomeFragment";
    private View mHomeFragmenView;
    private Button mVideoOpen, mTakePhoto;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
//    private FrameLayout mFramelayoutControlView;
    public static Handler mStateChangeHandler;
    public MySurfaceViewControler mMySurfaceViewControler;
    public MySurfaceViewHeadControler mMySurfaceViewHeadControler;
    private boolean isDeviceFind = false;  //通过DeviceControlFragment发现了设备
    private ControlModelChanged mControlModelChanged;
    public static Handler mImageDisplayHandler;
//    private boolean isStartYTXHandshake = false;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mHomeFragmenView =  inflater.inflate(R.layout.fragment_home,container,false);
        mVideoOpen = (Button)mHomeFragmenView.findViewById(R.id.open_video);
        mImageView = (ImageView)mHomeFragmenView.findViewById(R.id.xiaole_image);
        mProgressBar = (ProgressBar)mHomeFragmenView.findViewById(R.id.download_progressBar);
        mProgressBar.setVisibility(View.GONE);
        mMySurfaceViewControler = (MySurfaceViewControler)mHomeFragmenView.findViewById(R.id.control_view);
        mMySurfaceViewHeadControler = (MySurfaceViewHeadControler)mHomeFragmenView.findViewById(R.id.control_view_head);
//        mFramelayoutControlView = (FrameLayout)mHomeFragmenView.findViewById(R.id.framelayout_control_view);
        mMySurfaceViewControler.setVisibility(View.INVISIBLE);
        mMySurfaceViewHeadControler.setVisibility(View.INVISIBLE);
//        mFramelayoutControlView.setVisibility(View.INVISIBLE);
        mTakePhoto = (Button)mHomeFragmenView.findViewById(R.id.take_photo);
        //test to invisible take photo button
        mTakePhoto.setVisibility(View.INVISIBLE);

        final String id = getYTXContactID();
        Log.d("TIEJIANG", "HomeFragment---createView" + "YTX ID= " + id);
        mVideoOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id.equals("0")){
                    Toast.makeText(getActivity(), "id错误", Toast.LENGTH_LONG).show();
                }else{
                    CCPAppManager.callVoIPAction(getActivity(), ECVoIPCallManager.CallType.VIDEO,
                            id, id,false);
                }

            }
        });
        mVideoOpen.setVisibility(View.GONE);

        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMySurfaceViewControler.handleSendTextMessage(Constant.TAKE_PHOTO);
                receiveImageUrlAndDownload();
                mProgressBar.setVisibility(View.VISIBLE);
                displayImage();
                mTakePhoto.setVisibility(View.INVISIBLE);
            }
        });
        mTakePhoto.setVisibility(View.INVISIBLE);
        mStateChangeHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //检查云通讯的通讯状态－－－暂留作接口
//                boolean is_wlan_ok = mMySurfaceViewControler.isWLANOK;

                switch (msg.what){
                    case 0:
                        mMySurfaceViewControler.setVisibility(View.VISIBLE);
                        mMySurfaceViewHeadControler.setVisibility(View.VISIBLE);
                        mVideoOpen.setVisibility(View.VISIBLE);
//                        mTakePhoto.setVisibility(View.VISIBLE);
                        mTakePhoto.setVisibility(View.INVISIBLE);
                        isDeviceFind = true;

//                        Log.d("TIEJIANG", "state_change");
                        break;
                    case 1: //控制模式改变（局域网/外网）,设置为局域网控制
                        //第一次联网成功时候SurfaceView还未创建，因此mControlModelChanged还未在其中实例化
                        if (mControlModelChanged != null){
                            mControlModelChanged.isLocalNetControl(true);
                        }
                        break;
                    case 2:  //局域网控制模式被关闭，外网是否开启由握手信号决定（在MySurfaceViewControler）

                        if (mControlModelChanged != null){
                            mControlModelChanged.isLocalNetControl(false);
                        }
                        break;
                }
            }
        };



        //启动网络监听线程
//        isStartYTXHandshake = true;
//        new Thread(new YTXHandshakeRunnabel()).start();
        return mHomeFragmenView;
    }


    @Override
    public void init() {

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("TIEJIANG", "HomeFragment---onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("TIEJIANG", "HomeFragment---onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TIEJIANG", "HomeFragment---onDestroy");
//        isStartYTXHandshake = false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

//        mImageView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        if (hidden){
            mMySurfaceViewControler.setVisibility(View.INVISIBLE);
            mMySurfaceViewHeadControler.setVisibility(View.INVISIBLE);
            Log.d("TIEJIANG", "HomeFragment---onHiddenChanged" + " hidden= " + hidden);
        }else if (isDeviceFind){
            mMySurfaceViewControler.setVisibility(View.VISIBLE);
            mMySurfaceViewHeadControler.setVisibility(View.VISIBLE);
        }

//        isHomeFragmentHidden = hidden;
//        Log.d("TIEJIANG", "HomeFragment---onHiddenChanged" + " hidden= " + hidden);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("TIEJIANG", "HomeFragment---setUserVisibleHint()= " + isVisibleToUser);
        if(isVisibleToUser){

        }else {

        }
    }

//    class YTXHandshakeRunnabel implements Runnable{
//
//        @Override
//        public void run() {
//            //beginDrawing---surfaceView开始绘制的时候即开始判断网络情况
//            while (isStartYTXHandshake){
//                try {
//                    mMySurfaceViewControler.handleSendTextMessage(Constant.HAND_SHAKE);
//                    Thread.sleep(3000);
////                    Log.d("TIEJIANG", "MySurfaceViewControler---YTXHandshakeRunnabel YTXHandshake");
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//
//            }
//        }
//    }

    /**
     * function: display image
     *
     * */
    public void displayImage(){

        mImageDisplayHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mProgressBar.setVisibility(View.INVISIBLE); //progressBar dismiss
                mImageView.setVisibility(View.VISIBLE);
                switch (msg.what){
                    case 0:
                        if (((String)msg.obj).equals("down_failed")){
                            mImageView.setImageResource(R.drawable.xiaole_image_down_failed);
                        }
                        break;
                    case 1:  //缩略图
                        byte[] bitmapByte = (byte[])msg.obj;
                        Bitmap mBitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
                        mImageView.setImageBitmap(mBitmap);
                        mTakePhoto.setVisibility(View.VISIBLE);
                        break;
                    case 2: //完整图片
                        byte[] BigBitmapByte = (byte[])msg.obj;
                        Bitmap mBigBitmap = BitmapFactory.decodeByteArray(BigBitmapByte, 0, BigBitmapByte.length);
                        mImageView.setImageBitmap(mBigBitmap);
                        mTakePhoto.setVisibility(View.VISIBLE);
                        FileUtil.saveBitmap(mBigBitmap);
                        clearImageDisplay();
                        break;
                    case 3:  //clear image after 3s
                        mImageView.setVisibility(View.INVISIBLE);
                        break;

                }
            }
        };
    }

    /**
     * function: clear image display after 3s
     *
     * */
    private void clearImageDisplay(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                    mImageDisplayHandler.obtainMessage(3, "").sendToTarget();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * function: receive image url from ytx (callback)
     * and start to download image with okhttp
     *
     * */
    public void receiveImageUrlAndDownload(){

        //启动拍照监听回调
        mMySurfaceViewControler.getImageDowndURL(new ImageDownloadInterface() {
            @Override
            public void onImageDownload(final String image_url) {

                Log.d("TIEJIANG", "HomeFragment---"+" image_url="+image_url);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new HttpConnect().downImage(image_url);
                    }
                }).start();

            }

            @Override
            public void onImagethumbDownload(final String thumb_image_url) {
                Log.d("TIEJIANG", "HomeFragment---"+" thumb_image_url="+thumb_image_url);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new HttpConnect().downImage(thumb_image_url);
                    }
                }).start();
            }

        });
    }

    public MySurfaceViewControler getMySurfaceViewControlerInstance(){

        if (mMySurfaceViewControler != null){
            return mMySurfaceViewControler;
        }
        return null;
    }

    /**
     * function: tansmit interface instance to this.
     * call back method from MySurfaceViewControler.java
     * */
    public void changeControleModel(ControlModelChanged controlModelChanged){

        this.mControlModelChanged = controlModelChanged;
    }

    public String getYTXContactID(){

        //ＡＰＰ退到后台再回到前台的时候可能会出现类的实例被回收，此时就统一回到ＡＰＰ登入界面重新开始（获得实例）
        if (ActivityInstance.mSplashActivityInstance == null){
            Intent mIntent = new Intent(getActivity(), SplashActivity.class);
            startActivity(mIntent);
        }else {
            String[] id = ActivityInstance.mSplashActivityInstance.getYTXID();
            if (!id[1].equals("1") && id[1] != null){
                return id[1];
            }
        }

        return "0";
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//
////        if (getArguments().getBundle().getString("DEVICE_ON") != null){
////            String isDeviceOn = getArguments().getString("DEVICE_ON");
////            Log.d("TIEJIANG", "isDeviceOn= " + isDeviceOn);
////        }else if(getArguments().getString("DEVICE_ON") != null){
////            Log.d("TIEJIANG", "isDeviceOn= " + "device_off");
////        }
//
//    }

}
