package com.yinyutech.xiaolerobot;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mob.MobSDK;
import com.yinyutech.xiaolerobot.bean.User;
import com.yinyutech.xiaolerobot.common.CCPAppManager;
import com.yinyutech.xiaolerobot.core.ClientUser;
import com.yinyutech.xiaolerobot.helper.SDKCoreHelper;
import com.yinyutech.xiaolerobot.utils.UserLocalData;
import com.yuntongxun.ecsdk.ECInitParams;


public class XiaoLeApplication extends Application {

    private User user;
    String appKey = "8aaf070858cd982e0158e21ff0000cee";
    String token = "ca8bdec6e6ed3cc369b8122a1c19306d";
    String mobile = "71707102";
    String pass = "";
    ECInitParams.LoginAuthType mLoginAuthType = ECInitParams.LoginAuthType.NORMAL_AUTH;
    private static XiaoLeApplication mInstance;

    /**
     * 单例，返回一个实例
     * @return
     */
    public static XiaoLeApplication getInstance(){
        if (mInstance == null) {
            Log.d("TIEJIANG", "[Application] instance is null.");
        }
        Log.d("TIEJIANG", "[ECApplication] return instance succeed.");
        return  mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        CCPAppManager.setContext(mInstance);
        //save app key/ID and contact number etc. and init rong-lian-yun SDK
        ClientUser clientUser = new ClientUser(mobile);
        clientUser.setAppKey(appKey);
        clientUser.setAppToken(token);
        clientUser.setLoginAuthType(mLoginAuthType);
        clientUser.setPassword(pass);
        CCPAppManager.setClientUser(clientUser);
        SDKCoreHelper.init(this, ECInitParams.LoginMode.FORCE_LOGIN);

        initUser();
//        Fresco.initialize(this);  //facebook
        //sharedSDK 初始化
        MobSDK.init(getInstance(), "1f39c57121ae6", "62f9a52a61b3eaf0ab04d48189913694");
//        Log.d("TIEJIANG", "XIAOLEAPPLICATION INIT");
    }

    private void initUser(){

        this.user = UserLocalData.getUser(this);
    }

    public User getUser(){

        return user;
    }

    public void putUser(User user,String token){
        this.user = user;
        UserLocalData.putUser(this,user);
        UserLocalData.putToken(this,token);
    }

    public void clearUser(){
        this.user =null;
        UserLocalData.clearUser(this);
        UserLocalData.clearToken(this);

    }

    public String getToken(){

        return  UserLocalData.getToken(this);
    }

    private  Intent intent;
    public void putIntent(Intent intent){
        this.intent = intent;
    }

    public Intent getIntent() {
        return this.intent;
    }

    public void jumpToTargetActivity(Context context){

        context.startActivity(intent);
        this.intent =null;
    }


}
