package com.yinyutech.xiaolerobot.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.logger.Logger;
import com.yinyutech.xiaolerobot.model.AddBoxStatus;
import com.yinyutech.xiaolerobot.ui.activity.MainActivity;
import com.yinyutech.xiaolerobot.utils.soundbox.BoxUDPBroadcaster;
import com.yinyutech.xiaolerobot.utils.soundbox.SoundBoxManager;
import com.yinyutech.xiaolerobot.utils.soundbox.XiaoLeUDP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.yinyutech.xiaolerobot.R.id.linearLayout_second_step;
import static com.yinyutech.xiaolerobot.R.id.next_step;
import static com.yinyutech.xiaolerobot.R.id.pairTipTextView;
import static com.yinyutech.xiaolerobot.ui.fragment.HomeFragment.mStateChangeHandler;

public class DeviceControlFragment extends BaseFragment {


    public static final int ACTION_EDIT=1;
    public static final int ACTION_CAMPLATE=2;
    private static final String TAG = "TIEJIANG";
    private View mDeviceControlFragmentView;

    private Button nextStep, mButtonEnter, mButtonShare, mShowIsXiaoleExist;
    private EditText mWifiName, mWifiPwd;
    private SharedPreferences mWifiSharedPreferences;
    private int mStepFlag = 1;
    private ImageView mImageView;
    private TextView wifiInputHint, settingOver, mPairTipTextView;
    private TextView hintFirst, hintSecond, hintThird, hintFouth;
    private LinearLayout mLinearLayoutShowIsXiaoleExist ,mLinearLayoutScanXiaole ,mLinearLayoutSecond, mLinearLayoutFinalStep;
    private ProgressBar mNetProgressBar, mScanProgressBar;
    private final long shortTime = 1000;
    private final long longTime = 5000;
    private BoxUDPBroadcaster udpBroadcaster;  //调用BoxUDPBroadcaster有context构造函数，方便获取preference存储的值
    private XiaoLeUDP mXiaoLeUDP;
    public static Handler mScanXiaoLeHandler;
    private boolean isXiaoLeExist = false; // 搜索小乐是否存在
    private boolean isStartConnectNetModel = false;  //是否开始进入到联网模式
    //获得DeviceControlFragment 实例　（程序开始时候，此处还不能够获得ＤeviceControlFragment实例）
    private HomeFragment mHomeFragment = ActivityInstance.mMainActivityInstance.getHomeFragmentInstance();
    private DeviceControlFragment mDeviceControlFragment = ActivityInstance.mMainActivityInstance.getDeviceControlFragmentInstance();


    private Handler timerHandler = new Handler();

    private Runnable detectHotspot = new Runnable() {
        @Override
        public void run() {
            if (!connectToHotspot())
                return;

            SoundBoxManager manager = SoundBoxManager.getInstance();
            String ssid = manager.currentSSIDName();
//            Logger.v("detectHotspot SSID: %s", ssid);
            Log.d("TIEJIANG", "detectHotspot SSID: "  + ssid);
            Log.d("TIEJIANG", "detectHotspot--manager.isWiFiConnected()= " + manager.isWiFiConnected());

            if (SoundBoxManager.kBoxWiFiHotspotName.equals(ssid) && manager.isWiFiConnected()) {
//                statusTextView.setText("正在向音箱发送Wi-Fi连接信息...");
                setCurrentStep(2);
                mImageView.setBackgroundResource(R.drawable.net_progress_bar_third);

                manager.sendWifiInfoToBoxHotspot(new SoundBoxManager.SendWifiCompletion() {
                    @Override
                    public void onFinish(boolean isSuccess) {
                        if (isSuccess) {
                            // 重新连接原来的Wi-Fi网络
                            timerHandler.postDelayed(detectWiFi, shortTime);
                        } else {
                            timerHandler.postDelayed(detectHotspot, longTime);
                        }
                    }
                });
            } else {
                timerHandler.postDelayed(detectHotspot, longTime);
            }
        }
    };

    private Runnable detectWiFi = new Runnable() {
        @Override
        public void run() {
            if (!connectToOriginWiFi())
                return;

            SoundBoxManager manager = SoundBoxManager.getInstance();
            String ssid = manager.currentSSIDName();
            String[] ytxID = new String[2];
//            Logger.v("detectWiFi SSID: %s", ssid);
            Log.d("TIEJIANG", "detectWiFi SSID: "  + ssid);
            Log.d("TIEJIANG", "AddBoxStatus.getInstance().uploadWiFiName= " + AddBoxStatus.getInstance().uploadWiFiName);
            Log.d("TIEJIANG", "detectWiFi--manager.isWiFiConnected()= " + manager.isWiFiConnected());

//            statusTextView.setText("正在连接Wi-Fi网络...");
            setCurrentStep(3);
            mImageView.setBackgroundResource(R.drawable.net_progress_bar_fourth);

            if (AddBoxStatus.getInstance().uploadWiFiName.equals(ssid) && manager.isWiFiConnected()) {
                //（基于Ｈ３上面广佳的部分配置完毕）云通讯尚未配置ＯＫ
                setCurrentStep(4);
                mImageView.setBackgroundResource(R.drawable.net_progress_bar_fifth);

                //设置按键可用
                nextStep.setEnabled(true);
                //Progress 可见
                mNetProgressBar.setVisibility(View.INVISIBLE);
//                EventBus.getDefault().post(new AddBoxButtonEnableEvent(true, false));

//                statusTextView.setText("请点击下一步继续");
            } else {
                timerHandler.postDelayed(detectWiFi, longTime);

            }
        }
    };


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mDeviceControlFragmentView = inflater.inflate(R.layout.fragment_device_control,container,false);
        //在此生命周期方法内初始化才能够得到activity的实例
        udpBroadcaster = new BoxUDPBroadcaster(getActivity());  //调用BoxUDPBroadcaster有context构造函数，方便获取preference存储的值
        mXiaoLeUDP = new XiaoLeUDP(getActivity());
        initScanView();
        mHomeFragment.mMySurfaceViewControler.FirstStartYTXHandshake();
        startScanXiaoLe();
        analysis();

        return mDeviceControlFragmentView;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("TIEJIANG", "DeviceControlFragment---hidden= " + hidden + " isXiaoLeExist= " + isXiaoLeExist);

//        if (!hidden && isXiaoLeExist){
        //isStartConnectNetModel---不在联网模式的时候才搜索设备
        if (!hidden && !isStartConnectNetModel){
                startScanXiaoLe();
//                analysis();
        }

    }

    @Override
    public void init() {

    }

    public void analysis(){

        mScanXiaoLeHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                String scanMessage = (String)msg.obj;
                Log.d("TIEJIANG", "DeviceControlFragment---mScanXiaoLeHandler" + " scanMessage= " + scanMessage);
                mShowIsXiaoleExist.setVisibility(View.VISIBLE);
                mPairTipTextView.setVisibility(View.INVISIBLE);
                mScanProgressBar.setVisibility(View.INVISIBLE);
                //重新切换到DeviceControlFragment的时候会重新搜索设备，如果搜索到则要隐藏联网ＵＩ
                //未搜索到的情况则会通过按键进入到联网部分
                invisibleNetUI();
                // 处于联网模式则不进入
                if (!isStartConnectNetModel){
                    if (scanMessage.length() > 1){
                        //搜索到设备，直接进入到设备
                        String state = "";
                        String hostip = "";
                        String name = "";
                        try{
                            JSONObject parseH3json = new JSONObject(scanMessage);
                            state = parseH3json.getString("state");
                            hostip = parseH3json.getString("hostip");
                            name = parseH3json.getString("name");
                            Log.d("TIEJIANG", "DeviceControlFragment---mScanXiaoLeHandler" + " state= " + state + ", hostip= " + hostip + ", name= " + name);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        if (state.equals("local_handed") && name.equals("XiaoleServer") && hostip != null) {
//                            isXiaoLeExist = true;
//                            mShowIsXiaoleExist.setText("xiaole robot: " + hostip);
                            isXiaoLeExist = true;
                            mShowIsXiaoleExist.setText("xiaole robot: " + hostip);

                            //开启局域网控制模式
                            mStateChangeHandler.sendEmptyMessage(1);
                            Log.d("TIEJIANG", "DeviceControlFragment---analysis" + " udp handed");

                        }else if (state.equals("IDSetted") && name.equals("XiaoleServer") && hostip != null){
                            isXiaoLeExist = true;
                            mShowIsXiaoleExist.setText("xiaole robot: " + hostip);

                            //开启局域网控制模式
                            mStateChangeHandler.sendEmptyMessage(1);
                            Log.d("TIEJIANG", "DeviceControlFragment---analysis" + " IDset handed");
                        }
                    }else if (mHomeFragment.mMySurfaceViewControler.isWLANOK){  //外网ＯＫ且（即）云通讯ＯＫ
                        Log.d("TIEJIANG", "DeviceControlFragment---analysis"+" isWLANOK= TRUE");
                        //手机在远程情况下启动ＡＰＰ，并且首次进入到ＡＰＰ的时候搜索小乐
                        isXiaoLeExist = true;
                        mShowIsXiaoleExist.setText("xiaole robot");
                        mStateChangeHandler.sendEmptyMessage(2);  //关闭局域网控制模式
                    }else {
                        //同时外网也不通，则判断设备掉线
                        mShowIsXiaoleExist.setText("未发现设备,请进入联网模式");
                        isXiaoLeExist = false;
                        mLinearLayoutFinalStep.setVisibility(View.INVISIBLE);
                        mStateChangeHandler.sendEmptyMessage(2);  //关闭局域网控制模式

                    }
                }
            }
        };
    }

    public void initScanView(){

        mImageView = (ImageView)mDeviceControlFragmentView.findViewById(R.id.progress_img_fist);
        mImageView.setVisibility(View.INVISIBLE);
        nextStep = (Button)mDeviceControlFragmentView.findViewById(next_step);
        nextStep.setVisibility(View.INVISIBLE);

        mWifiName = (EditText)mDeviceControlFragmentView.findViewById(R.id.wifi_user);
        mWifiPwd = (EditText)mDeviceControlFragmentView.findViewById(R.id.wifi_pwd);
        wifiInputHint = (TextView)mDeviceControlFragmentView.findViewById(R.id.wifi_input_hint);
        mWifiName.setVisibility(View.INVISIBLE);
        mWifiPwd.setVisibility(View.INVISIBLE);
        wifiInputHint.setVisibility(View.INVISIBLE);

        mLinearLayoutSecond = (LinearLayout)mDeviceControlFragmentView.findViewById(linearLayout_second_step);
        hintFirst = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_first);
        hintSecond = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_second);
        hintThird = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_third);
        hintFouth = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_fouth);
        mLinearLayoutSecond.setVisibility(View.GONE);

        settingOver = (TextView)mDeviceControlFragmentView.findViewById(R.id.setting_over);
        settingOver.setVisibility(View.GONE);

        mLinearLayoutFinalStep = (LinearLayout)mDeviceControlFragmentView.findViewById(R.id.linearLayout_final_step);
        mButtonEnter = (Button)mDeviceControlFragmentView.findViewById(R.id.button_enter);
        mButtonShare = (Button)mDeviceControlFragmentView.findViewById(R.id.button_share);
        mLinearLayoutFinalStep.setVisibility(View.GONE);
        mNetProgressBar = (ProgressBar)mDeviceControlFragmentView.findViewById(R.id.net_progressBar);
        mNetProgressBar.setVisibility(View.INVISIBLE);

        mLinearLayoutScanXiaole = (LinearLayout)mDeviceControlFragmentView.findViewById(R.id.linearLayout_scan_xiaole);
        mScanProgressBar = (ProgressBar)mDeviceControlFragmentView.findViewById(R.id.pairProgressBar);
        mPairTipTextView = (TextView)mDeviceControlFragmentView.findViewById(pairTipTextView);
//        mLinearLayoutShowIsXiaoleExist = (LinearLayout)mDeviceControlFragmentView.findViewById(R.id.linearLayout_show_is_xiaole_exist);

        mShowIsXiaoleExist = (Button)mDeviceControlFragmentView.findViewById(R.id.show_is_xiaole_exist);
        mShowIsXiaoleExist.setVisibility(View.INVISIBLE);

        mShowIsXiaoleExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScanProgressBar.setVisibility(View.INVISIBLE);
                if(isXiaoLeExist){
                    settingOK();
                }else{
                    //点击"确认"前，先触摸小乐进入联网模式
                    showEnterNetConnectModel();

                }
            }
        });
    }

    public void showEnterNetConnectModel(){

        isStartConnectNetModel = true;
        AlertDialog.Builder mVersionDialog = new AlertDialog.Builder(getActivity());
        mVersionDialog.setTitle("提示");
        mVersionDialog.setMessage("点击\"确认\"按钮前，请先确认小乐已经进入联网模式");
        mVersionDialog.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //没有扫描到设备，进入连接和配对模式
                udpBroadcaster.stopBroadcastSearchBox(); //开始联网步骤之后就停止Ｈ３设备的扫描
                initDeviceView();
                showAddBoxFullStepActivity(getActivity());
                mWifiName.setText(AddBoxStatus.getInstance().uploadWiFiName);
                mShowIsXiaoleExist.setVisibility(View.INVISIBLE);

            }
        }).create();
        mVersionDialog.show();
    }

    public void startScanXiaoLe(){
        udpBroadcaster.startBroadcastSearchBox();
    }

    private void invisibleNetUI(){

        mImageView.setVisibility(View.INVISIBLE);
        nextStep.setVisibility(View.INVISIBLE);
        mWifiName.setVisibility(View.INVISIBLE);
        mWifiPwd.setVisibility(View.INVISIBLE);
        wifiInputHint.setVisibility(View.INVISIBLE);
        settingOver.setVisibility(View.INVISIBLE);
//        mLinearLayoutFinalStep.setVisibility(View.INVISIBLE);

    }

    public void initDeviceView(){

        mImageView.setVisibility(View.VISIBLE);
        nextStep.setVisibility(View.VISIBLE);
        mWifiName.setVisibility(View.VISIBLE);
        mWifiPwd.setVisibility(View.VISIBLE);
        wifiInputHint.setVisibility(View.VISIBLE);

        // 联网配对步骤
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String wifiName = mWifiName.getText().toString().trim();
//                String wifiPwd = mWifiPwd.getText().toString().trim();
//                //保存wifi信息
//                mWifiSharedPreferences = getActivity().getSharedPreferences(Constant.WIFI_MESSAGE, Context.MODE_PRIVATE);
//                SharedPreferences.Editor mEditor = mWifiSharedPreferences.edit();
//                mEditor.putString(Constant.WIFI_NAME, wifiName);
//                mEditor.putString(Constant.WIFI_PWD, wifiPwd);
//                mEditor.commit();

                switch (mStepFlag){
                    case 1:
                        //获得WIFI密码
                        AddBoxStatus abs = AddBoxStatus.getInstance();
                        abs.uploadWiFiPassword = mWifiPwd.getText().toString().trim();

                        mWifiName.setVisibility(View.GONE);
                        mWifiPwd.setVisibility(View.GONE);
                        wifiInputHint.setVisibility(View.GONE);
                        //第二步组件可见
                        mNetProgressBar.setVisibility(View.VISIBLE);
                        mLinearLayoutSecond.setVisibility(View.VISIBLE);
                        mImageView.setBackgroundResource(R.drawable.net_progress_bar_second);

                        //热点->配对->完成
//                        mLinearLayoutSecond.setVisibility(View.INVISIBLE);
                        setCurrentStep(1);
                        //设置按键不可用
//                        nextStep.setBackgroundColor(Color.GRAY);
                        nextStep.setEnabled(false);
                        // 先停止
                        stopStepWork();
                        timerHandler.postDelayed(detectHotspot, shortTime);

                        mStepFlag = 2;
                        Log.d(TAG, "DeviceControlFragment---mStepFlag= " + mStepFlag);

                        break;
                    case 2:
                        //绑定---设置云通讯ID

                        //设置完成
                        mLinearLayoutSecond.setVisibility(View.GONE);
                        nextStep.setVisibility(View.GONE);
                        settingOver.setVisibility(View.VISIBLE);
                        mLinearLayoutFinalStep.setVisibility(View.VISIBLE);
                        isXiaoLeExist = true;
                        //开始发送云通讯ＩＤ到Ｈ３
                        mXiaoLeUDP.startXiaoLeUDP();
                        startScanXiaoLe();
                        isStartConnectNetModel = false; //联网完成　退出联网模式flag

                        mStepFlag = 1;
                        Log.d(TAG, "mStepFlag= " + mStepFlag);

                        break;
//                    case 3:
//
//                        mStepFlag = 4;
//                        Log.d(TAG, "mStepFlag= " + mStepFlag);
//                        break;
                }

                //test code
//                CCPAppManager.callVoIPAction(getActivity(), ECVoIPCallManager.CallType.VIDEO,
//                        "20170717", "20170717",false);
            }
        });

        mButtonEnter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //完成设置跳转到视频界面
                settingOK();
            }
        });
        mButtonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    public void refData(){

    }

    private void settingOK(){
        Message mMessage = new Message();
        mMessage.what = 0;
        mMessage.obj = "setting_ok";
        MainActivity.mTabhostSkipHandler.sendMessage(mMessage);
        mStateChangeHandler.sendEmptyMessage(0);
    }

    // 添加音箱完整流程，当音箱未连接Wi-Fi时使用
    public static void showAddBoxFullStepActivity(Context context) {
        AddBoxStatus abs = AddBoxStatus.getInstance();
        abs.initialAddBoxStatus();
        abs.uploadWiFiName = SoundBoxManager.getInstance().currentSSIDName();

//        Intent intent = new Intent(context, AddBoxActivity.class);
//        if (clearTop)
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
    }


    private void stopStepWork() {
        timerHandler.removeCallbacks(detectHotspot);
        timerHandler.removeCallbacks(detectWiFi);
    }

    private boolean connectToHotspot() {
        Activity activity = getActivity();
        if (activity == null)
            return false;

        WifiManager wifiManager = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        WifiConfiguration wifiConfig = null;
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SoundBoxManager.kBoxWiFiHotspotName+ "\"")) {
                wifiConfig = i;
                break;
            }
        }

        if (wifiConfig == null) {
            wifiConfig = new WifiConfiguration();
            wifiConfig.SSID = String.format("\"%s\"", SoundBoxManager.kBoxWiFiHotspotName);
        }

        wifiConfig.preSharedKey = String.format("\"%s\"", SoundBoxManager.kBoxWiFiHotspotPassword);

        int hotspotNetworkID = wifiManager.addNetwork(wifiConfig);

        boolean enableSuccess = wifiManager.enableNetwork(hotspotNetworkID, true);
        Logger.v("connect to smartbox: %d %s", hotspotNetworkID, (enableSuccess) ? "success" : "fail");

        return true;
    }

    private boolean connectToOriginWiFi() {
        Activity activity = getActivity();
        if (activity == null)
            return false;

        WifiManager wifiManager = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();

        WifiConfiguration wifiConfig = null;
        for( WifiConfiguration i : list ) {
            Log.d("TIEJIANG", "DeviceControlFragement---connectToOriginWiFi" + "wifi list= " + i.SSID);
            if(i.SSID != null && i.SSID.equals("\"" + AddBoxStatus.getInstance().uploadWiFiName + "\"")) {
                wifiConfig = i;
                Log.d("TIEJIANG", "DeviceControlFragement---connectToOriginWiFi" + " matched ssid= " + wifiConfig.SSID);
                Log.d("TIEJIANG", "DeviceControlFragment---connectToOriginWiFi" + " wifiConfig.networkId= "+ wifiConfig.networkId);
                break;
            }

        }

        if (wifiConfig != null) {
            boolean isSuccess = wifiManager.enableNetwork(wifiConfig.networkId, true);
            int connectState = wifiManager.getConfiguredNetworks().get(wifiConfig.networkId).status;
            Log.d("TIEJIANG", "DeviceControlFragment---connectToOriginWiFi" + " connectState= "+ connectState);
            // isSuccess 如果返回true只是代表wifiManager去执行连接网络的指令了,并不代表已经连接上网络
            Log.d("TIEJIANG", "DeviceControlFragment---connectToOriginWiFi" + " wifiConfig.networkId= "+ wifiConfig.networkId);
//            Logger.v("connect to origin Wifi: %s", isSuccess ? "success" : "fail");
            Log.d("TIEJIANG con ori wifi: ", isSuccess ? "success" : "fail");
        } else {
//            Logger.v("Origin Wifi config missing");
            Log.d("TIEJIANG", "connectToOriginWiFi---" + " Origin Wifi config missing");
        }

        return true;
    }

    private void setCurrentStep(int step) {
        if (step == 1) {
            hintFirst.setTextColor(getResources().getColor(R.color.Black_60));
            hintSecond.setTextColor(getResources().getColor(R.color.blue));
            hintThird.setTextColor(getResources().getColor(R.color.blue));
            hintFouth.setTextColor(getResources().getColor(R.color.blue));
        } else if (step == 2) {
            hintFirst.setTextColor(getResources().getColor(R.color.Black_60));
            hintSecond.setTextColor(getResources().getColor(R.color.Black_60));
            hintThird.setTextColor(getResources().getColor(R.color.blue));
            hintFouth.setTextColor(getResources().getColor(R.color.blue));
        } else if (step == 3) {
            hintFirst.setTextColor(getResources().getColor(R.color.Black_60));
            hintSecond.setTextColor(getResources().getColor(R.color.Black_60));
            hintThird.setTextColor(getResources().getColor(R.color.Black_60));
            hintFouth.setTextColor(getResources().getColor(R.color.blue));
        } else {
            hintFirst.setTextColor(getResources().getColor(R.color.Black_60));
            hintSecond.setTextColor(getResources().getColor(R.color.Black_60));
            hintThird.setTextColor(getResources().getColor(R.color.Black_60));
            hintFouth.setTextColor(getResources().getColor(R.color.Black_60));
        }
    }
}
