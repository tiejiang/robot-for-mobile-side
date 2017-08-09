package com.yinyutech.xiaolerobot.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yinyutech.xiaolerobot.XiaoLeApplication;
import com.yinyutech.xiaolerobot.bean.User;


public class BaseActivity extends AppCompatActivity {


    protected static final String TAG = BaseActivity.class.getSimpleName();

    public void startActivity(Intent intent,boolean isNeedLogin){


        if(isNeedLogin){

            User user = XiaoLeApplication.getInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{
//                XiaoLeApplication.getInstance().putIntent(intent);
//                Intent loginIntent = new Intent(this
//                        , RegistActivity.class);
//                super.startActivity(intent);
            }

        }
        else{
            super.startActivity(intent);
        }

    }
    /**
     * 通过类名启动Activity
     *
     * @param pClass
     */
    protected void openActivity(Class<?> pClass) {
        openActivity(pClass, null);
    }
    /**
     * 通过类名启动Activity，并且含有Bundle数据
     *
     * @param pClass
     * @param pBundle
     */
    protected void openActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(this, pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        startActivity(intent);
    }
}
