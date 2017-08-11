package com.yinyutech.xiaolerobot.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.ui.activity.MainActivity;
import com.yinyutech.xiaolerobot.utils.Constant;

import static com.yinyutech.xiaolerobot.R.id.next_step;

public class DeviceControlFragment extends BaseFragment {


    public static final int ACTION_EDIT=1;
    public static final int ACTION_CAMPLATE=2;
    private static final String TAG = "DeviceControlFragment";
    private View mDeviceControlFragmentView;

    private Button nextStep, mButtonEnter, mButtonShare;
    private EditText mWifiName, mWifiPwd;
    private SharedPreferences mWifiSharedPreferences;
    private int mStepFlag = 1;
    private ImageView mImageView;
    private TextView wifiInputHint, settingOver;

    private LinearLayout mLinearLayoutSecond, mLinearLayoutFinalStep;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mDeviceControlFragmentView = inflater.inflate(R.layout.fragment_device_control,container,false);
        initDeviceView();
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
        mLinearLayoutSecond.setVisibility(View.GONE);
        settingOver = (TextView)mDeviceControlFragmentView.findViewById(R.id.setting_over);
        settingOver.setVisibility(View.GONE);
        mLinearLayoutFinalStep = (LinearLayout)mDeviceControlFragmentView.findViewById(R.id.linearLayout_final_step);
        mButtonEnter = (Button)mDeviceControlFragmentView.findViewById(R.id.button_enter);
        mButtonShare = (Button)mDeviceControlFragmentView.findViewById(R.id.button_share);
        mLinearLayoutFinalStep.setVisibility(View.GONE);


        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wifiName = mWifiName.getText().toString().trim();
                String wifiPwd = mWifiPwd.getText().toString().trim();
                //保存wifi信息
                mWifiSharedPreferences = getActivity().getSharedPreferences(Constant.WIFI_MESSAGE, Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mWifiSharedPreferences.edit();
                mEditor.putString(Constant.WIFI_NAME, wifiName);
                mEditor.putString(Constant.WIFI_PWD, wifiPwd);
                mEditor.commit();
                switch (mStepFlag){
                    case 1:
                        //WIFI密码（和名称）
                        mWifiName.setVisibility(View.GONE);
                        mWifiPwd.setVisibility(View.GONE);
                        wifiInputHint.setVisibility(View.GONE);
                        //第二步组件可见
                        mLinearLayoutSecond.setVisibility(View.VISIBLE);
                        mImageView.setBackgroundResource(R.drawable.net_progress_bar_second);
                        mStepFlag = 2;
                        Log.d(TAG, "mStepFlag= " + mStepFlag);

                        break;
                    case 2:
                        //热点->配对->完成
                        mLinearLayoutSecond.setVisibility(View.INVISIBLE);
                        mStepFlag = 3;
                        Log.d(TAG, "mStepFlag= " + mStepFlag);

                        break;
                    case 3:
                        //绑定---设置云通讯ID

                        mStepFlag = 4;
                        Log.d(TAG, "mStepFlag= " + mStepFlag);
                        break;
                    case 4:
                        //设置完成
                        mLinearLayoutSecond.setVisibility(View.GONE);
                        nextStep.setVisibility(View.GONE);
                        settingOver.setVisibility(View.VISIBLE);
                        mLinearLayoutFinalStep.setVisibility(View.VISIBLE);
                        mImageView.setBackgroundResource(R.drawable.net_progress_bar_fifth);


                        mStepFlag = 5;
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

}
