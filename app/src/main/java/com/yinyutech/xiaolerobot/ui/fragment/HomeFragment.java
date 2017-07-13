package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yinyutech.xiaolerobot.R;

public class HomeFragment extends BaseFragment {


//    @ViewInject(R.id.slider)
//    private SliderLayout mSliderLayout;


//    @ViewInject(R.id.recyclerview)
//    private RecyclerView mRecyclerView;

//    private HomeCatgoryAdapter mAdatper;


    private static  final  String TAG="HomeFragment";


//    private Gson mGson = new Gson();

//    private List<Banner> mBanner;



//    private OkHttpHelper httpHelper = OkHttpHelper.getInstance();



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

    //main page content
//    private void initRecyclerView() {
//
//
//        httpHelper.get(Contants.API.CAMPAIGN_HOME, new BaseCallback<List<HomeCampaign>>() {
//            @Override
//            public void onBeforeRequest(Request request) {
//
//            }
//
//            @Override
//            public void onFailure(Request request, Exception e) {
//
//            }
//
//            @Override
//            public void onResponse(Response response) {
//
//            }
//
//            @Override
//            public void onSuccess(Response response, List<HomeCampaign> homeCampaigns) {
//
//                initData(homeCampaigns);
//            }
//
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//
//            @Override
//            public void onTokenError(Response response, int code) {
//
//            }
//        });
//
//    }


//    private  void initData(List<HomeCampaign> homeCampaigns){


//        mAdatper = new HomeCatgoryAdapter(homeCampaigns,getActivity());
//
//        mAdatper.setOnCampaignClickListener(new HomeCatgoryAdapter.OnCampaignClickListener() {
//            @Override
//            public void onClick(View view, Campaign campaign) {
//
//
//                Intent intent = new Intent(getActivity(), WareListActivity.class);
//                intent.putExtra(Contants.COMPAINGAIN_ID,campaign.getId());
//
//                startActivity(intent);
//
//
//            }
//        });
//
//        mRecyclerView.setAdapter(mAdatper);
//
//        mRecyclerView.addItemDecoration(new CardViewtemDecortion());
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();

//        mSliderLayout.stopAutoCycle();
    }
}
