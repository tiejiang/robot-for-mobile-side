package com.yinyutech.xiaolerobot.ui.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.bean.Tab;
import com.yinyutech.xiaolerobot.bean.UserInfo;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.ui.fragment.DeviceControlFragment;
import com.yinyutech.xiaolerobot.ui.fragment.HomeFragment;
import com.yinyutech.xiaolerobot.ui.fragment.OptionFragment;
import com.yinyutech.xiaolerobot.ui.widget.FragmentTabHost;
import com.yinyutech.xiaolerobot.utils.soundbox.SoundBoxManager;
import com.yinyutech.xiaolerobot.utils.soundbox.SoundBoxServiceAction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private LayoutInflater mInflater;
    private static FragmentTabHost mTabhost;
    private DeviceControlFragment mDeviceControlFragment;
    private HomeFragment mHomeFragment;
    private List<Tab> mTabs = new ArrayList<>(3);
    public static Handler mTabhostSkipHandler;
//    public HomeFragCallBack mHomeFragCallBack;
//    public MySurfaceViewControler mMySurfaceViewControler;
    Tab tab_home ;
    Tab tab_deviceControl ;
    Tab tab_option ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityInstance.mMainActivityInstance = this; //传递MainActivity实例到工厂类
//        mMySurfaceViewControler = (MySurfaceViewControler)findViewById(R.id.control_view);
//        mMySurfaceViewControler.setVisibility(View.GONE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // 第二步：初始化音乐和音箱服务
        SoundBoxServiceAction.getInstance().setupContext(getApplicationContext());
        SoundBoxManager.getInstance().setupContext(getApplicationContext());
        UserInfo.sharedUserInfo().setupSharedUserInfo(getApplicationContext());
        // 第三步：注册事件监听
//        EventBus.getDefault().register(this);
        // 第四步：启动音箱发现服务 (21239端口被打开做监听数据)
//        startService(new Intent(this, UDPServerService.class));
        initTab();
//        Handler mHandler = new Handler(){
//
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//
//                if(mHomeFragment == null){
//                    //test code
//                    String tabStr = mTabhost.getCurrentTabTag();
//                    Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tabStr);
//                    if(fragment ==null){
//                        mHomeFragment= (HomeFragment) fragment;
//                    }
//                }
//                Bundle mBundle = new Bundle();
//                mBundle.putString("DEVICE", "device_off");
////        mHomeFragment = new HomeFragment();
//                mHomeFragment.setArguments(mBundle);
//            }
//        };

        //when xiaole net connect model setted(in the deviceControlFragment)
        mTabhostSkipHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        if (((String)msg.obj).equals("setting_ok")){
                            //启动HomeFragment
                            mTabhost.setCurrentTab(0);

//                            mTabhost.setEnabled(false); //for test

                            //设置对应ｂｕｔｔｏｎ为ｃｈｅｃｋｅｄ状态
//                        ((RadioButton)findViewById(R.id.radio_button0)).setChecked(true);
//                            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.xiaole));
//                            Bundle mBundle = new Bundle();
//                            mBundle.putString("DEVICE_ON", "device_on");

//                            mHomeFragment.setArguments(mBundle);
                        }

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        };
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.d("TIEJIANG", "MainActivity---onAttachFragment");

    }

    private void initTab() {

        tab_home = new Tab(HomeFragment.class,R.string.xiaole,R.drawable.bottom_xiaole);
        tab_deviceControl = new Tab(DeviceControlFragment.class,R.string.device_control,R.drawable.bottom_net);
        tab_option = new Tab(OptionFragment.class,R.string.option,R.drawable.bottom_option);

        mTabs.add(tab_home);
        mTabs.add(tab_deviceControl);
        mTabs.add(tab_option);

        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        for (Tab tab : mTabs){
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));
            tabSpec.setIndicator(buildIndicator(tab));
            mTabhost.addTab(tabSpec,tab.getFragment(),null);
        }
        //test code  fragment = null !
//        String tabStr = mTabhost.getCurrentTabTag();
//        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(tabStr);

        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(tabId==getString(R.string.device_control)){
                    Log.d("TIEJIANG", "fragment change---device_control");
//                    mMySurfaceViewControler.setVisibility(View.GONE);

//                    refData();
                }else if (tabId==getString(R.string.xiaole)){
                    Log.d("TIEJIANG", "fragment change---xiaole");
//                    mMySurfaceViewControler.setVisibility(View.VISIBLE);
                }else if (tabId==getString(R.string.option)){
                    Log.d("TIEJIANG", "fragment change---option");
//                    mMySurfaceViewControler.setVisibility(View.GONE);

                }
            }
        });

        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);
    }

    public DeviceControlFragment refData(){

        if(mDeviceControlFragment == null){
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.device_control));
            Log.d("TIEJIANG", "MainAcitivity---refData" + " fragment= " + fragment);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.device_control));
                    if(fragment !=null){
                        mDeviceControlFragment= (DeviceControlFragment) fragment;
//                        deviceControlFragment.refData();
                    }
                    Log.d("TIEJIANG", "MainAcitivity---refData delay50s " + " mDeviceControlFragment= " + mDeviceControlFragment);
                }
            }, 50);

        }
//        else{
//            deviceControlFragment.refData();
//        }
        return mDeviceControlFragment;
    }

    public HomeFragment getHomeFragmentInstance(){

        if(mHomeFragment == null){
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.xiaole));
            Log.d("TIEJIANG", "MainAcitivity---refData" + " fragment= " + fragment);
            if (fragment != null){
                mHomeFragment = (HomeFragment)fragment;
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.xiaole));
                        if(fragment !=null){
                            mHomeFragment= (HomeFragment) fragment;
                        }
                        Log.d("TIEJIANG", "MainAcitivity---refData delay50s " + " HomeFragment= " + mHomeFragment);
                    }
                }, 50);
            }
        }
        return mHomeFragment;
    }

    public DeviceControlFragment getDeviceControlFragmentInstance(){

        if(mDeviceControlFragment == null){
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.device_control));
            Log.d("TIEJIANG", "MainAcitivity---refData" + " fragment= " + fragment);
            if (fragment != null){
                mDeviceControlFragment = (DeviceControlFragment) fragment;
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.device_control));
                        if(fragment !=null){
                            mDeviceControlFragment= (DeviceControlFragment) fragment;
                        }
                        Log.d("TIEJIANG", "MainAcitivity---refData delay50s " + " mDeviceControlFragment= " + mDeviceControlFragment);
                    }
                }, 50);
            }
        }
        return mDeviceControlFragment;
    }

    private View buildIndicator(Tab tab){

        View view = mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());

        return  view;
    }


    // fragment-home  和 MainActivity之间的回调机制,尚未调试通过
    public void getHomeFragCallBack(HomeFragCallBack homeFragCallBack){
        homeFragCallBack.isfragmentChange(true);
    }

    public interface HomeFragCallBack{
        void isfragmentChange(boolean change);
    }

}
