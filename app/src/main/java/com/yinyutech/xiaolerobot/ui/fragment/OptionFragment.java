package com.yinyutech.xiaolerobot.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.ui.activity.OPtionAlbumActivity;
import com.yinyutech.xiaolerobot.ui.activity.SplashActivity;

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
    private LinearLayout mOptionUser, mOptionAlbum, mOptionVersion, mOptionSetting;
    private Context mOptionFragmentInstance;
    /*获取当前系统的android版本号*/
    int currentapiVersion=android.os.Build.VERSION.SDK_INT;
    private static String phoneMessage = "手机型号: " + android.os.Build.MODEL + "\n系统版本: " + android.os.Build.VERSION.RELEASE;
    private SplashActivity mSplashActivity = ActivityInstance.mSplashActivityInstance;
    private String userID = mSplashActivity.getUserID();

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOptionFragmentInstance = getActivity();
        mOptionFragmentView = inflater.inflate(R.layout.fragment_option,container,false);
        initSourceView();
        Log.d("TIEJIANG", "OptionFragment---createView"+" userID= "+userID);
        return mOptionFragmentView;

    }

    @Override
    public void init() {
        mOptionUser = (LinearLayout)mOptionFragmentView.findViewById(R.id.option_user);
        mOptionAlbum = (LinearLayout)mOptionFragmentView.findViewById(R.id.option_album);
        mOptionVersion = (LinearLayout)mOptionFragmentView.findViewById(R.id.option_version);
        mOptionSetting = (LinearLayout)mOptionFragmentView.findViewById(R.id.option_setting);

        mOptionUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userStr = "当前用户: ";
                String user_id = "当前用户: " + userID;
                showVersionData(userStr, user_id);
            }
        });

        mOptionAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mIntent  = new Intent(mOptionFragmentInstance, OPtionAlbumActivity.class);
                startActivity(mIntent);

            }
        });

        mOptionVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String versionStr = "版本信息";
                String message =  phoneMessage + "\n" + "小乐版本: " + getAppVersionName(mOptionFragmentInstance);

                showVersionData(versionStr, message);

            }
        });

        mOptionSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void initSourceView(){
//        mOptionUser = (ImageView)mOptionFragmentView.findView
    }

    public void showVersionData(String title_meg, String message){

        AlertDialog.Builder mVersionDialog = new AlertDialog.Builder(mOptionFragmentInstance);

        mVersionDialog.setTitle(title_meg);
        mVersionDialog.setMessage(message);
        mVersionDialog.setIcon(R.drawable.version_icon);
        mVersionDialog.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        }).create();
        mVersionDialog.show();
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


}



