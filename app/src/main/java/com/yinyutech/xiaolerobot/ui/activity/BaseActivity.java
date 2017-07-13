package com.yinyutech.xiaolerobot.ui.activity;

import android.content.Intent;
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
//                        , LoginActivity.class);
//                super.startActivity(intent);

            }

        }
        else{
            super.startActivity(intent);
        }

    }
}
