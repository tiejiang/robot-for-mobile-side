package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yinyutech.xiaolerobot.R;

public class HomeFragment extends BaseFragment {



    private static  final  String TAG="HomeFragment";

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return    inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void init() {

//        requestImages();

//        initRecyclerView();
    }

    

    @Override
    public void onDestroy() {
        super.onDestroy();

//        mSliderLayout.stopAutoCycle();
    }
}
