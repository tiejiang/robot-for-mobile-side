package com.yinyutech.xiaolerobot.ui.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.yinyutech.xiaolerobot.R;

public class SplashActivity extends BaseActivity {

	public static final String TAG = SplashActivity.class.getSimpleName();

	private ImageView mSplashItem_iv = null;
	private Button login, register;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		Constants.SCREEN_DENSITY = metrics.density;
//		Constants.SCREEN_HEIGHT = metrics.heightPixels;
//		Constants.SCREEN_WIDTH = metrics.widthPixels;
//
//		mHandler = new Handler(getMainLooper());
		findViewById();
		initView();

		login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (whetherLogin()){
					openActivity(MainActivity.class);
				}
			}
		});
		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity(RegistActivity.class);
			}
		});
	}

	//查询本地用户信息，匹配则进入主界面，如果没有则进入注册界面
	public boolean whetherLogin(){

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.itau.tmall.ui.base.BaseActivity#findViewById()
	 */
	protected void findViewById() {
		// TODO Auto-generated method stub
		mSplashItem_iv = (ImageView) findViewById(R.id.splash_loading_item);
		login = (Button)findViewById(R.id.login);
		register = (Button)findViewById(R.id.regist);

		login.setVisibility(View.INVISIBLE);
		register.setVisibility(View.INVISIBLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itau.jingdong.ui.base.BaseActivity#initView()
	 */
	protected void initView() {
		// TODO Auto-generated method stub
		Animation translate = AnimationUtils.loadAnimation(this,
				R.anim.splash_loading);
		translate.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub

				//判断登录信息进入到不同界面（登录注册/主界面）
				login.setVisibility(View.VISIBLE);
				register.setVisibility(View.VISIBLE);

////				openActivity(MenuActivity.class);
//				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//				SplashActivity.this.finish();
			}
		});
		mSplashItem_iv.setAnimation(translate);
	}





}
