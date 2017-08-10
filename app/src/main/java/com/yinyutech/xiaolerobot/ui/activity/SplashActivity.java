package com.yinyutech.xiaolerobot.ui.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.utils.Constant;

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
				}else {
					Log.d("TIEJIANG", "SplashActivity---没有注册信息");
                    Toast.makeText(SplashActivity.this, "请您先注册", Toast.LENGTH_LONG).show();
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
        //判断是否存在注册信息
        SharedPreferences sp = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
        String number = sp.getString(Constant.USER_NUMBER, "0");
        String security = sp.getString(Constant.USER_SERCURITY, "1");
        Log.d("TIEJIANG", "SplashActivity---NUMBER= " + number + " SECURITY= " + security);
        if (number.equals("0") || security.equals("1")){
            return false;
        }else{
            return true;
        }
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
