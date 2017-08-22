package com.yinyutech.xiaolerobot.ui.fragment;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yinyutech.xiaolerobot.R;

import static com.yinyutech.xiaolerobot.common.CCPAppManager.getPackageName;

public class OptionFragment extends BaseFragment {

    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;
    private long category_id=0;


    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;
    private View mOptionFragmentView;
    private ImageView mOptionUser, mOptionAlbum, mOptionVersion, mOptionSetting;
    private Context mOptionFragmentInstance;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOptionFragmentInstance = getActivity();
        mOptionFragmentView = inflater.inflate(R.layout.fragment_option,container,false);
        initSourceView();

        return mOptionFragmentView;

    }

    @Override
    public void init() {
        mOptionUser = (ImageView)mOptionFragmentView.findViewById(R.id.option_user);
        mOptionAlbum = (ImageView)mOptionFragmentView.findViewById(R.id.option_album);
        mOptionVersion = (ImageView)mOptionFragmentView.findViewById(R.id.option_version);
        mOptionSetting = (ImageView)mOptionFragmentView.findViewById(R.id.option_setting);

        mOptionUser.setOnClickListener(mOptionFragmentInstance);
    }

    private void initSourceView(){
        mOptionUser = (ImageView)mOptionFragmentView.findView
    }
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versionCode;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 返回当前程序版本号
     */
    private String getVersionName() throws Exception
    {
        // 获取packagemanager的实例
        PackageManager packageManager = getActivity().getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
        String version = packInfo.versionName;
        return version;
    }

    /*获取当前系统的android版本号*/
    int currentapiVersion=android.os.Build.VERSION.SDK_INT;
}



