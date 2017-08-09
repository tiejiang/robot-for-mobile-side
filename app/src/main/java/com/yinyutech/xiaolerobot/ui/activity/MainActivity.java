package com.yinyutech.xiaolerobot.ui.activity;

import android.os.Bundle;
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
import com.yinyutech.xiaolerobot.ui.fragment.DeviceControlFragment;
import com.yinyutech.xiaolerobot.ui.fragment.OptionFragment;
import com.yinyutech.xiaolerobot.ui.fragment.HomeFragment;
import com.yinyutech.xiaolerobot.ui.widget.FragmentTabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private LayoutInflater mInflater;
    private FragmentTabHost mTabhost;
    private DeviceControlFragment deviceControlFragment;
    private List<Tab> mTabs = new ArrayList<>(3);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        welcom = (Button)findViewById(R.id.welcom);
//        welcom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mIntent = new Intent(MainActivity.this, YunZhiShengActivity.class);
//                startActivity(mIntent);
//
//            }
//        });

        initTab();
    }

    private void initTab() {

        Tab tab_home = new Tab(HomeFragment.class,R.string.xiaole,R.drawable.bottom_xiaole);
        Tab tab_deviceControl = new Tab(DeviceControlFragment.class,R.string.device_control,R.drawable.bottom_net);
        Tab tab_option = new Tab(OptionFragment.class,R.string.option,R.drawable.bottom_option);

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

        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(tabId==getString(R.string.device_control)){
                    Log.d("TIEJIANG", "fragment change---device_control");
                    refData();
                }
            }
        });

        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);
    }

    private void refData(){

        if(deviceControlFragment == null){
            Fragment fragment =  getSupportFragmentManager().findFragmentByTag(getString(R.string.device_control));
            if(fragment !=null){
                deviceControlFragment= (DeviceControlFragment) fragment;
                deviceControlFragment.refData();
            }
        }
        else{
            deviceControlFragment.refData();
        }
    }

    private View buildIndicator(Tab tab){

        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());

        return  view;
    }

}
