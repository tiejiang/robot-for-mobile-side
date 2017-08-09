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

    //banner request
    private  void requestImages(){

//        String url ="http://112.124.22.238:8081/course_api/banner/query?type=1";
//
//
//
//        httpHelper.get(url, new SpotsCallBack<List<Banner>>(getActivity()){
//
//
//            @Override
//            public void onSuccess(Response response, List<Banner> banners) {
//
//                mBanner = banners;
//                initSlider();
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

//        mSliderLayout.stopAutoCycle();
    }
}
