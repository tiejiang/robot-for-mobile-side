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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.bean.ClientUser;
import com.yinyutech.xiaolerobot.common.CCPAppManager;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.helper.SDKCoreHelper;
import com.yinyutech.xiaolerobot.utils.Constant;
import com.yinyutech.xiaolerobot.utils.DemoUtils;
import com.yuntongxun.ecsdk.ECInitParams;

import static com.yinyutech.xiaolerobot.utils.DemoUtils.generateMixString;

public class SplashActivity extends BaseActivity
//		implements IMChattingHelper.OnMessageReportCallback
{

	public static final String TAG = SplashActivity.class.getSimpleName();

	private ImageView mSplashItem_iv = null;
	private Button login, register;
	private SharedPreferences mYTXIDSharedPreference;
	private RelativeLayout mAnimRelativeLayout1;

	String pass = "";
	ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		ActivityInstance.mSplashActivityInstance = this;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		Constants.SCREEN_DENSITY = metrics.density;
//		Constants.SCREEN_HEIGHT = metrics.heightPixels;
//		Constants.SCREEN_WIDTH = metrics.widthPixels;
//
//		mHandler = new Handler(getMainLooper());

		saveYunTXID();
		String[] ytxID = getYTXID();
		initYTX(ytxID[0]);
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

	public void initYTX(String mobile){

		//save app key/ID and contact number etc. and init rong-lian-yun SDK
		ClientUser clientUser = new ClientUser(mobile);
		clientUser.setAppKey(Constant.appKey);
		clientUser.setAppToken(Constant.token);
		clientUser.setLoginAuthType(mLoginAuthType);
		clientUser.setPassword(pass);
		CCPAppManager.setClientUser(clientUser);
		SDKCoreHelper.init(SplashActivity.this, ECInitParams.LoginMode.FORCE_LOGIN);
//		IMChattingHelper.setOnMessageReportCallback(SplashActivity.this);
		Log.d("TIEJIANG", "SplashActivity---initYTX" + " mobile= " + mobile);
	}

	public String[] getYTXID(){

		String[] YTXID = new String[2];
		SharedPreferences sp = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
		String mobileXiaoLe = sp.getString(Constant.XIAOLE_YTX_MOBILE, "0");
		String H3XiaoLe = sp.getString(Constant.XIAOLE_YTX_H3, "1");
		YTXID[0] = mobileXiaoLe;
		YTXID[1] = H3XiaoLe;

		return YTXID;
	}

	public String getUserID(){

//		test code to get sharedPreference value
		SharedPreferences sp = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
		String number = sp.getString(Constant.USER_NUMBER, "0");
		String security = sp.getString(Constant.USER_SERCURITY, "1");
		Log.d("TIEJIANG", "SplashActivity---NUMBER= " + number + " SECURITY= " + security);

		return number;
	}

	private void saveYunTXID(){

		//先寻找是否存在ＩＤ，没有则调用方法生成ＩＤ，然后存储
		SharedPreferences sp = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
		String mobileXiaoLe = sp.getString(Constant.XIAOLE_YTX_MOBILE, "0");
		String H3XiaoLe = sp.getString(Constant.XIAOLE_YTX_H3, "1");
		Log.d("TIEJIANG", "SplashActivity---mobileXiaoLe= " + mobileXiaoLe + " H3XiaoLe= " + H3XiaoLe);

		if (mobileXiaoLe.equals("0") && H3XiaoLe.equals("1")){
			String randomNumber = DemoUtils.getRandomNumber(5);
			String randomChar = generateMixString(5);
			String mobileID = randomNumber + randomChar + "xiaolemobile";
			String H3ID = randomNumber + randomChar + "xiaoleH3";
			//保存注册用户信息
			mYTXIDSharedPreference = getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
			SharedPreferences.Editor mEditor = mYTXIDSharedPreference.edit();
			mEditor.putString(Constant.XIAOLE_YTX_MOBILE, mobileID);
			mEditor.putString(Constant.XIAOLE_YTX_H3, H3ID);
			mEditor.commit();
		}

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
		mAnimRelativeLayout1 = (RelativeLayout)findViewById(R.id.relativeLayout1);
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
				Log.d("TIEJIANG", "SplashActivity---onAnimationStart");

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				Log.d("TIEJIANG", "SplashActivity---onAnimationRepeat");

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Log.d("TIEJIANG", "SplashActivity---onAnimationEnd");
				mAnimRelativeLayout1.setVisibility(View.GONE);
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


//    @Override
//    public void onMessageReport(ECError error, ECMessage message) {
//
//    }
//
//    @Override
//    public void onPushMessage(String sessionId, List<ECMessage> msgs) {
//
//    }
}
