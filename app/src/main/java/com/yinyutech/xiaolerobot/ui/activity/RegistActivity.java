package com.yinyutech.xiaolerobot.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yinyutech.xiaolerobot.R;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by yinyu-tiejiang on 17-7-13.
 */

public class RegistActivity extends Activity {

    private EditText et_number;
    private String number;
    private EditText et_security;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        et_number = (EditText) findViewById(R.id.et_phone);
        et_security = (EditText) findViewById(R.id.et_security);
        //注册短信回调监听
        SMSSDK.registerEventHandler(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //要在activity销毁时反注册，否侧会造成内存泄漏问题
        SMSSDK.unregisterAllEventHandler();
    }
    /**
     * 短信验证的回调监听
     */
    private EventHandler ev = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Log.d("TIEJIANG", "GO INTO EVENTHANDLER " + "result= " + result + " data= " + data.toString());
            if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                //提交验证码成功,如果验证成功会在data里返回数据。data数据类型为HashMap<number,code>
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    Log.d("TIEJIANG", "提交验证码成功" + data.toString());
                    HashMap<String, Object> mData = (HashMap<String, Object>) data;
                    String country = (String) mData.get("country");//返回的国家编号
                    String phone = (String) mData.get("phone");//返回用户注册的手机号

                    Log.d("TIEJIANG", "COUNTRY+PHONE= " + country + "====" + phone);

                    if (phone.equals(number)) {
                        runOnUiThread(new Runnable() {//更改ui的操作要放在主线程，实际可以发送handler
                            @Override
                            public void run() {
                                showDailog("恭喜你！通过验证");
                                //注册成功，进入到主界面
                                Intent mIntent = new Intent(RegistActivity.this, MainActivity.class);
                                startActivity(mIntent);
                                dialog.dismiss();
                                //    Toast.makeText(MainActivity.this, "通过验证", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDailog("验证失败");
                                dialog.dismiss();
                                //     Toast.makeText(MainActivity.this, "验证失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                    Log.d("TIEJIANG", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表

                }
            } else {
                ((Throwable) data).printStackTrace();
//                if (result == SMSSDK.RESULT_ERROR){
//
//                }
            }
        }
    };
    //验证结果弹窗
    private void showDailog(String text) {

        new AlertDialog.Builder(RegistActivity.this)
                .setTitle(text)
                .setPositiveButton("确定", null)
                .show();
    }
    /**
     * 获取验证码
     * @param v
     */
    public void getSecurity(View v) {
        number = et_number.getText().toString().trim();
        //发送短信，传入国家号和电话---使用SMSSDK核心类之前一定要在MyApplication中初始化，否侧不能使用
        if (TextUtils.isEmpty(number)) {
            Toast.makeText(this, "号码不能为空！", Toast.LENGTH_SHORT).show();
        } else {
            SMSSDK.getVerificationCode("+86", number);
            Toast.makeText(this, "发送成功:" + number, Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * function :注册
     * 向服务器提交验证码，在监听回调中判断是否通过验证
     * @param v
     */
    public void testSecurity(View v) {
        String security = et_security.getText().toString();
        if (!TextUtils.isEmpty(security)) {
            dialog = ProgressDialog.show(this, null, "正在验证...", false, true);
            //提交短信验证码
            SMSSDK.submitVerificationCode("+86", number, security);//国家号，手机号码，验证码
            Toast.makeText(this, "提交了注册信息:" + number, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}

