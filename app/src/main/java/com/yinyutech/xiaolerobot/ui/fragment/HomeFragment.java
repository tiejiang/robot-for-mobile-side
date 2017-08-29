package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.common.CCPAppManager;
import com.yinyutech.xiaolerobot.entrance.ControlModelChanged;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.ui.activity.MySurfaceViewControler;
import com.yuntongxun.ecsdk.ECVoIPCallManager;


/**
 * author　tiejiang
 * 20170810
 * */
public class HomeFragment extends BaseFragment{

    private static  final  String TAG="HomeFragment";
    private View mHomeFragmenView;
    private Button mVideoOpen;
//    private FrameLayout mFramelayoutControlView;
    public static Handler mStateChangeHandler;
    public MySurfaceViewControler mMySurfaceViewControler;
    private boolean isDeviceFind = false;  //通过DeviceControlFragment发现了设备
    private ControlModelChanged mControlModelChanged;
//    private boolean isStartYTXHandshake = false;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mHomeFragmenView =  inflater.inflate(R.layout.fragment_home,container,false);
        mVideoOpen = (Button)mHomeFragmenView.findViewById(R.id.open_video);
        mMySurfaceViewControler = (MySurfaceViewControler)mHomeFragmenView.findViewById(R.id.control_view);
//        mFramelayoutControlView = (FrameLayout)mHomeFragmenView.findViewById(R.id.framelayout_control_view);
        mMySurfaceViewControler.setVisibility(View.INVISIBLE);
//        mFramelayoutControlView.setVisibility(View.INVISIBLE);
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

        mStateChangeHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //检查云通讯的通讯状态－－－暂留作接口
//                boolean is_wlan_ok = mMySurfaceViewControler.isWLANOK;

                switch (msg.what){
                    case 0:
                        mMySurfaceViewControler.setVisibility(View.VISIBLE);
                        mVideoOpen.setVisibility(View.VISIBLE);
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

        if (hidden){
            mMySurfaceViewControler.setVisibility(View.INVISIBLE);
            Log.d("TIEJIANG", "HomeFragment---onHiddenChanged" + " hidden= " + hidden);
        }else if (isDeviceFind){
            mMySurfaceViewControler.setVisibility(View.VISIBLE);
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

    public void changeControleModel(ControlModelChanged controlModelChanged){

        this.mControlModelChanged = controlModelChanged;
    }

    public String getYTXContactID(){

        String[] id = ActivityInstance.mSplashActivityInstance.getYTXID();
        if (!id[1].equals("1") && id[1] != null){
            return id[1];
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
