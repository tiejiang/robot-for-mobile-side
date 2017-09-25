package com.yinyutech.xiaolerobot.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.model.AddBoxStatus;
import com.yinyutech.xiaolerobot.utils.soundbox.SoundBoxManager;

import java.util.List;

import static com.yinyutech.xiaolerobot.R.id.linearLayout_second_step;

public class AddBoxHotspotFragment extends Fragment {

    private TextView hintFirst, hintSecond, hintThird, hintFouth;
    private ProgressBar mNetProgressBar;
    private ImageView mImageView;
    private LinearLayout mLinearLayoutSecond;
    private TextView statusTextView;
    private ProgressBar progressBar;

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
//                nextStep.setEnabled(true);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mDeviceControlFragmentView = inflater.inflate(R.layout.fragment_add_box_hotspot, container, false);

        mImageView = (ImageView)mDeviceControlFragmentView.findViewById(R.id.progress_img_fist);
        mLinearLayoutSecond = (LinearLayout)mDeviceControlFragmentView.findViewById(linearLayout_second_step);
        hintFirst = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_first);
        hintSecond = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_second);
        hintThird = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_third);
        hintFouth = (TextView)mDeviceControlFragmentView.findViewById(R.id.hint_fouth);
        mLinearLayoutSecond.setVisibility(View.GONE);

        mNetProgressBar = (ProgressBar)mDeviceControlFragmentView.findViewById(R.id.net_progressBar);


        return mDeviceControlFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

//        EventBus.getDefault().post(new AddBoxButtonEnableEvent(false, false));
        startStepWork();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopStepWork();
    }

    private void startStepWork() {
//        progressBar.setVisibility(View.VISIBLE);
//        statusTextView.setText("正在连接音箱热点...");
        setCurrentStep(1);

        // 先停止
        stopStepWork();
        timerHandler.postDelayed(detectHotspot, shortTime);
    }

    private void stopStepWork() {
        timerHandler.removeCallbacks(detectHotspot);
        timerHandler.removeCallbacks(detectWiFi);
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
//        Logger.v("connect to smartbox: %d %s", hotspotNetworkID, (enableSuccess) ? "success" : "fail");

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
//            int connectState = wifiManager.getConfiguredNetworks().get(wifiConfig.networkId).status;
//            Log.d("TIEJIANG", "DeviceControlFragment---connectToOriginWiFi" + " connectState= "+ connectState);
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
}
