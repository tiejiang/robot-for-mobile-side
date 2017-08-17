package com.yinyutech.xiaolerobot.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.yinyutech.xiaolerobot.model.AddBoxStatus;
import com.yinyutech.xiaolerobot.ui.activity.MainActivity;
import com.yinyutech.xiaolerobot.utils.Constant;
import com.yinyutech.xiaolerobot.utils.soundbox.SoundBoxManager;
import com.yinyutech.xiaolerobot.logger.Logger;

import java.util.List;

import static com.yinyutech.xiaolerobot.R.id.next_step;

public class DeviceControlFragment extends BaseFragment {


    public static final int ACTION_EDIT=1;
    public static final int ACTION_CAMPLATE=2;
    private static final String TAG = "TIEJIANG";
    private View mDeviceControlFragmentView;

    private Button nextStep, mButtonEnter, mButtonShare;
    private EditText mWifiName, mWifiPwd;
    private SharedPreferences mWifiSharedPreferences;
    private int mStepFlag = 1;
    private ImageView mImageView;
    private TextView wifiInputHint, settingOver;
    private TextView hintFirst, hintSecond, hintThird, hintFouth;
    private LinearLayout mLinearLayoutSecond, mLinearLayoutFinalStep;
    private ProgressBar mNetProgressBar;
    private final long shortTime = 1000;
    private final long longTime = 5000;

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
//            Logger.v("detectWiFi SSID: %s", ssid);
            Log.d("TIEJIANG", "detectWiFi SSID: "  + ssid);
            Log.d("TIEJIANG", "AddBoxStatus.getInstance().uploadWiFiName= " + AddBoxStatus.getInstance().uploadWiFiName);
            Log.d("TIEJIANG", "detectWiFi--manager.isWiFiConnected()= " + manager.isWiFiConnected());

//            statusTextView.setText("正在连接Wi-Fi网络...");
            setCurrentStep(3);
            mImageView.setBackgroundResource(R.drawable.net_progress_bar_fourth);

            if (AddBoxStatus.getInstance().uploadWiFiName.equals(ssid) && manager.isWiFiConnected()) {
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
        initDeviceView();
        showAddBoxFullStepActivity(getActivity());
        mWifiName.setText(AddBoxStatus.getInstance().uploadWiFiName);

        return mDeviceControlFragmentView;
    }

    @Override
    public void init() {

    }

    public void initDeviceView(){
        mImageView = (ImageView)mDeviceControlFragmentView.findViewById(R.id.progress_img_fist);
        nextStep = (Button)mDeviceControlFragmentView.findViewById(next_step);
        mWifiName = (EditText)mDeviceControlFragmentView.findViewById(R.id.wifi_user);
        mWifiPwd = (EditText)mDeviceControlFragmentView.findViewById(R.id.wifi_pwd);
        wifiInputHint = (TextView)mDeviceControlFragmentView.findViewById(R.id.wifi_input_hint);
        mLinearLayoutSecond = (LinearLayout)mDeviceControlFragmentView.findViewById(R.id.linearLayout_second_step);
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
//                        mLinearLayoutSecond.setVisibility(View.GONE);
                        nextStep.setVisibility(View.GONE);
                        settingOver.setVisibility(View.VISIBLE);
                        mLinearLayoutFinalStep.setVisibility(View.VISIBLE);
//                        mImageView.setBackgroundResource(R.drawable.net_progress_bar_fifth);

                        mStepFlag = 3;
                        Log.d(TAG, "mStepFlag= " + mStepFlag);

                        break;
                    case 3:

                        mStepFlag = 4;
                        Log.d(TAG, "mStepFlag= " + mStepFlag);
                        break;
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
                Message mMessage = new Message();
                mMessage.what = 0;
                mMessage.obj = "setting_ok";
                MainActivity.mTabhostSkipHandler.sendMessage(mMessage);
                HomeFragment.mStateChangeHandler.sendEmptyMessage(0);

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
            if(i.SSID != null && i.SSID.equals("\"" + AddBoxStatus.getInstance().uploadWiFiName + "\"")) {
                wifiConfig = i;
                break;
            }
        }

        if (wifiConfig != null) {
            boolean isSuccess = wifiManager.enableNetwork(wifiConfig.networkId, true);

            Logger.v("connect to origin Wifi: %s", isSuccess ? "success" : "fail");
        } else {
            Logger.v("Origin Wifi config missing");
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

//            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
