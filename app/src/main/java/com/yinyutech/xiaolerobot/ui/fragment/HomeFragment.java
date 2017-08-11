package com.yinyutech.xiaolerobot.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.yinyutech.xiaolerobot.R;


/**
 *
 * 20170810
 * */
public class HomeFragment extends BaseFragment{

    private static  final  String TAG="HomeFragment";
    private View mHomeFragmenView;
    private Button mStarControl;
    public static Handler mStateChangeHandler;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mHomeFragmenView =  inflater.inflate(R.layout.fragment_home,container,false);
        mStarControl = (Button)mHomeFragmenView.findViewById(R.id.start_control);
        mStarControl.setVisibility(View.GONE);

        mStateChangeHandler = new Handler(){

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
                        mStarControl.setVisibility(View.VISIBLE);
                        Log.d("TIEJIANG", "state_change");

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }
        };

        return mHomeFragmenView;
    }

    @Override
    public void init() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

//        if (getArguments().getBundle().getString("DEVICE_ON") != null){
//            String isDeviceOn = getArguments().getString("DEVICE_ON");
//            Log.d("TIEJIANG", "isDeviceOn= " + isDeviceOn);
//        }else if(getArguments().getString("DEVICE_ON") != null){
//            Log.d("TIEJIANG", "isDeviceOn= " + "device_off");
//        }

    }
}
