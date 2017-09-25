package com.yinyutech.xiaolerobot.ui.activity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.anton46.stepsview.StepsView;
import com.lezhi.soundbox.model.AddBoxButtonEnableEvent;
import com.lezhi.soundbox.model.AddBoxStatus;
import com.orhanobut.logger.Logger;
import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.ui.fragment.AddBoxFinishFragment;
import com.yinyutech.xiaolerobot.ui.fragment.AddBoxHotspotFragment;
import com.yinyutech.xiaolerobot.ui.fragment.AddBoxPairFragment;
import com.yinyutech.xiaolerobot.ui.fragment.AddBoxWiFiPasswordFragment;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class AddBoxActivity extends FragmentActivity {
    private StepsView mStepsView;
    private String[] steps = {"WiFi密码", "音箱热点", "音箱配对", "完成"};
    private Button buttonNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_box);

        EventBus.getDefault().register(this);

        buttonNext = (Button)findViewById(R.id.buttonNext);

        mStepsView = (StepsView)findViewById(R.id.stepsView);
        mStepsView.setLabels(steps)
                .setBarColorIndicator(this.getResources().getColor(R.color.highlighted_text_material_light))
                .setProgressColorIndicator(this.getResources().getColor(R.color.ThemeYellow))
                .setLabelColorIndicator(this.getResources().getColor(R.color.material_blue_grey_900))
                .setCompletedPosition(0)
                .drawView();

        switchFragment(new AddBoxWiFiPasswordFragment());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        connectToOriginWiFi();
    }

    @Override
    public void onBackPressed() {
        // 阻止返回按键，必须连接音箱才能使用其它功能
    }

    private boolean connectToOriginWiFi() {
        WifiManager wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
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

    @Subscribe
    public void onEventMainThread(AddBoxButtonEnableEvent event) {
        buttonNext.setEnabled(event.enabled);

        if (event.enabled && event.performClick)
            buttonNext.performClick();
    }

    public void onNextButtonClicked(View view) {
        int position = mStepsView.getCompletedPosition() + 1;
        if (position >= steps.length) {
            finish();
        } else {
            updateStepsViewWithPosition(position);
            updateFragmentWithPosition(position);
        }
    }

    private void updateStepsViewWithPosition(int position) {
        if (0 <= position && position < steps.length) {
            mStepsView.setCompletedPosition(position).drawView();
        }
    }

    private void updateFragmentWithPosition(int position) {
        String nextButtonTitle = "下一步";
        switch (position) {
            case 0:
                switchFragment(new AddBoxWiFiPasswordFragment());
                break;
            case 1:
                switchFragment(new AddBoxHotspotFragment());
                break;
            case 2:
                switchFragment(new AddBoxPairFragment());
                break;
            case 3:
                switchFragment(new AddBoxFinishFragment());
                nextButtonTitle = "完成";
                break;
        }

        buttonNext.setText(nextButtonTitle);
    }

    private void switchFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        // don't add to BackStack
        // transaction.addToBackStack(null);
        transaction.commit();
    }
}
