package com.yinyutech.xiaolerobot.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lezhi.soundbox.model.AddBoxButtonEnableEvent;
import com.lezhi.soundbox.model.AddBoxStatus;
import com.lezhi.soundbox.util.SoundBoxManager;
import com.orhanobut.logger.Logger;

import java.util.List;

import de.greenrobot.event.EventBus;

public class AddBoxHotspotFragment extends Fragment {
    private TextView tip1TextView;
    private TextView tip2TextView;
    private TextView tip3TextView;
    private TextView tip4TextView;
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
            Logger.v("detectHotspot SSID: %s", ssid);

            if (SoundBoxManager.kBoxWiFiHotspotName.equals(ssid) && manager.isWiFiConnected()) {
                statusTextView.setText("正在向音箱发送Wi-Fi连接信息...");
                setCurrentStep(2);

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
            Logger.v("detectWiFi SSID: %s", ssid);

            statusTextView.setText("正在连接Wi-Fi网络...");
            setCurrentStep(3);

            if (AddBoxStatus.getInstance().uploadWiFiName.equals(ssid) && manager.isWiFiConnected()) {
                setCurrentStep(4);
                EventBus.getDefault().post(new AddBoxButtonEnableEvent(true, false));

                statusTextView.setText("请点击下一步继续");
            } else {
                timerHandler.postDelayed(detectWiFi, longTime);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_box_hotspot, container, false);

        tip1TextView = (TextView)v.findViewById(R.id.tip1TextView);
        tip2TextView = (TextView)v.findViewById(R.id.tip2TextView);
        tip3TextView = (TextView)v.findViewById(R.id.tip3TextView);
        tip4TextView = (TextView)v.findViewById(R.id.tip4TextView);
        statusTextView = (TextView)v.findViewById(R.id.statusTextView);
        progressBar = (ProgressBar)v.findViewById(R.id.hotspotProgressBar);

        tip1TextView.setText("1. 连接到音箱热点: " + SoundBoxManager.kBoxWiFiHotspotName);
        tip2TextView.setText("2. 向音箱发送Wi-Fi信息");
        tip3TextView.setText("3. 重新连接到您的Wi-Fi网络: " + AddBoxStatus.getInstance().uploadWiFiName);
        tip4TextView.setText("4. 音箱语音提示网络连接成功之后，点击下一步");

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().post(new AddBoxButtonEnableEvent(false, false));
        startStepWork();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopStepWork();
    }

    private void startStepWork() {
        progressBar.setVisibility(View.VISIBLE);
        statusTextView.setText("正在连接音箱热点...");
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
            tip1TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip2TextView.setTextColor(getResources().getColor(R.color.black75PercentColor));
            tip3TextView.setTextColor(getResources().getColor(R.color.black75PercentColor));
            tip4TextView.setTextColor(getResources().getColor(R.color.black75PercentColor));
        } else if (step == 2) {
            tip1TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip2TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip3TextView.setTextColor(getResources().getColor(R.color.black75PercentColor));
            tip4TextView.setTextColor(getResources().getColor(R.color.black75PercentColor));
        } else if (step == 3) {
            tip1TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip2TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip3TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip4TextView.setTextColor(getResources().getColor(R.color.black75PercentColor));
        } else {
            tip1TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip2TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip3TextView.setTextColor(getResources().getColor(R.color.Black_60));
            tip4TextView.setTextColor(getResources().getColor(R.color.Black_60));

            progressBar.setVisibility(View.INVISIBLE);
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
}
