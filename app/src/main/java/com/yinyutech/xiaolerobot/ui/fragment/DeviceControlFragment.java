package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.common.CCPAppManager;
import com.yuntongxun.ecsdk.ECVoIPCallManager;

public class DeviceControlFragment extends BaseFragment {


    public static final int ACTION_EDIT=1;
    public static final int ACTION_CAMPLATE=2;
    private static final String TAG = "DeviceControlFragment";
    private View mDeviceControlFragmentView;

    private Button nextStep;


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
        nextStep = (Button)mDeviceControlFragmentView.findViewById(R.id.next_step);
        //test code
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CCPAppManager.callVoIPAction(getActivity(), ECVoIPCallManager.CallType.VIDEO,
                        "20170717", "20170717",false);
            }
        });

    }
    public void refData(){

    }

}
