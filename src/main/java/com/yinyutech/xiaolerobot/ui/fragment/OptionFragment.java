package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yinyutech.xiaolerobot.R;

public class OptionFragment extends BaseFragment {

    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;
    private long category_id=0;


    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_option,container,false);
    }

    @Override
    public void init() {

//        requestCategoryData();
//        requestBannerData();
//
//        initRefreshLayout();
    }


//    private  void initRefreshLayout(){
//
//        mRefreshLaout.setLoadMore(true);
//        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
//            @Override
//            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
//
//                refreshData();
//
//            }
//
//            @Override
//            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
//
//                if(currPage <=totalPage)
//                    loadMoreData();
//                else{
////                    Toast.makeText()
//                    mRefreshLaout.finishRefreshLoadMore();
//                }
//            }
//        });
//    }
//
//
//    private  void refreshData(){
//
//        currPage =1;
//
//        state=STATE_REFREH;
//        requestWares(category_id);
//
//    }
//
//    private void loadMoreData(){
//
//        currPage = ++currPage;
//        state = STATE_MORE;
//        requestWares(category_id);
//
//    }
//
//
//    private  void requestCategoryData(){
//
//
//
//        mHttpHelper.get(Contants.API.CATEGORY_LIST, new SpotsCallBack<List<Category>>(getActivity()) {
//
//
//            @Override
//            public void onSuccess(Response response, List<Category> categories) {
//
//                showCategoryData(categories);
//
//                if(categories !=null && categories.size()>0)
//                    category_id = categories.get(0).getId();
//                    requestWares(category_id);
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });
//
//    }
//
//    private  void showCategoryData(List<Category> categories){
//
//
//        mCategoryAdapter = new CategoryAdapter(getActivity(),categories);
//
//        mCategoryAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                Category category = mCategoryAdapter.getItem(position);
//
//                category_id = category.getId();
//                currPage=1;
//                state=STATE_NORMAL;
//
//                requestWares(category_id);
//
//
//            }
//        });
//
//        mRecyclerView.setAdapter(mCategoryAdapter);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
//
//
//    }
//
//    private void requestBannerData( ) {
//
//
//
//       String url = Contants.API.BANNER+"?type=1";
//
//        mHttpHelper.get(url, new SpotsCallBack<List<Banner>>(getActivity()){
//
//
//            @Override
//            public void onSuccess(Response response, List<Banner> banners) {
//
//                showSliderViews(banners);
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//        });
//
//    }
//
//
//
//    private void showSliderViews(List<Banner> banners){
//
//
//
//
//        if(banners !=null){
//
//            for (Banner banner : banners){
//
//
//                DefaultSliderView sliderView = new DefaultSliderView(this.getActivity());
//                sliderView.image(banner.getImgUrl());
//                sliderView.description(banner.getName());
//                sliderView.setScaleType(BaseSliderView.ScaleType.Fit);
//                mSliderLayout.addSlider(sliderView);
//
//            }
//        }
//        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//
//        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
//        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
//        mSliderLayout.setDuration(3000);
//    }
//
//
//
//    private void requestWares(long categoryId){
//
//        String url = Contants.API.WARES_LIST+"?categoryId="+categoryId+"&curPage="+currPage+"&pageSize="+pageSize;
//
//        mHttpHelper.get(url, new SimpleCallback<Page<Wares>>(getActivity()) {
//
//            @Override
//            public void onSuccess(Response response, Page<Wares> waresPage) {
//
//
//                currPage = waresPage.getCurrentPage();
//                totalPage =waresPage.getTotalPage();
//
//                showWaresData(waresPage.getList());
//
//
//            }
//
//            @Override
//            public void onError(Response response, int code, Exception e) {
//
//            }
//
//
//        });
//
//    }
//
//    private  void showWaresData(List<Wares> wares){
//
//        switch (state){
//
//            case  STATE_NORMAL:
//
//                if(mWaresAdatper ==null) {
//                    mWaresAdatper = new WaresAdapter(getActivity(), wares);
//                    mWaresAdatper.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(View view, int position) {
//                            Wares wares = mWaresAdatper.getItem(position);
//
//                            Intent intent = new Intent(getActivity(), WareDetailActivity.class);
//
//                            intent.putExtra(Contants.WARE,wares);
//                            startActivity(intent);
//
//                        }
//                    });
//
//                    mRecyclerviewWares.setAdapter(mWaresAdatper);
//
//                    mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//                    mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
////                    mRecyclerviewWares.addItemDecoration(new DividerGridItemDecoration(getContext()));
//                }
//                else{
//                    mWaresAdatper.clear();
//                    mWaresAdatper.addData(wares);
//                }
//
//                break;
//
//            case STATE_REFREH:
//                mWaresAdatper.clear();
//                mWaresAdatper.addData(wares);
//
//                mRecyclerviewWares.scrollToPosition(0);
//                mRefreshLaout.finishRefresh();
//                break;
//
//            case STATE_MORE:
//                mWaresAdatper.addData(mWaresAdatper.getDatas().size(),wares);
//                mRecyclerviewWares.scrollToPosition(mWaresAdatper.getDatas().size());
//                mRefreshLaout.finishRefreshLoadMore();
//                break;
//        }
//    }
}



