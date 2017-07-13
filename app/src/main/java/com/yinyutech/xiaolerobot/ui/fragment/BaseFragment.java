package com.yinyutech.xiaolerobot.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yinyutech.xiaolerobot.XiaoLeApplication;
import com.yinyutech.xiaolerobot.bean.User;

public abstract class BaseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = createView(inflater,container,savedInstanceState);

//        ViewUtils.inject(this, view);

        initToolBar();

        init();

        return view;

    }

    public void  initToolBar(){

    }


    public abstract View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void init();



    public void startActivity(Intent intent,boolean isNeedLogin){


        if(isNeedLogin){

            User user = XiaoLeApplication.getInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{

//                XiaoLeApplication.getInstance().putIntent(intent);
//                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
//                super.startActivity(loginIntent);

            }

        }
        else{
            super.startActivity(intent);
        }

    }


}
