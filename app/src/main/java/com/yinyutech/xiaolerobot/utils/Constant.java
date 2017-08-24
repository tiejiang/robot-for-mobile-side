package com.yinyutech.xiaolerobot.utils;

import java.util.Vector;

public class Constant {

    //云通讯ｋｅｙ
    public static final String appKey = "8aaf070858cd982e0158e21ff0000cee";
    public static final String token = "ca8bdec6e6ed3cc369b8122a1c19306d";

    public static final String PREF_PASSWORD_VALUE = "input_password_value";
    public static final String PREF_EMAIL_VALUE = "input_email_value";
    public static final String ACTIVITY_RESULT = "result";
    
    public static final int REQUEST_CODE = 1;
    public static final int ERROE_NUM = 5;

    //用户注册信息以及云通讯ＩＤ的sharedPreference
    public static final String USER_MESSAGE = "user_data";
    public static final String USER_NUMBER = "user_number";
    public static final String USER_SERCURITY = "user_security";

    //小乐移动端和Ｈ３平台的云通讯ＩＤ
    public static final String XIAOLE_YTX_MOBILE = "ytx_id_mobile";
    public static final String XIAOLE_YTX_H3 = "ytx_id_h3";

    //WIFI信息
    public static final String WIFI_MESSAGE = "user_data";
    public static final String WIFI_NAME = "wifi_name";
    public static final String WIFI_PWD = "wifi_pwd";
    
    public static Vector<String> mNotEncryptionApp;

    public static final String BEGING_CONFIRM = "begin_confirm";  //移动端收到H3回发的确认开始发送指令
    public static final String BEGING_SEND = "query_begin";  //移动端请求开始发送运动控制指令
    public static final String MOBILE_FORWARD = "mobile_forward";
    public static final String MOBILE_BACK = "mobile_back";
    public static final String MOBILE_TURN_LEFT = "mobile_turn_left";
    public static final String MOBILE_TURN_RIGHTT = "mobile_turn_right";


    public static void filter(){
    	mNotEncryptionApp = new Vector<String>();
        mNotEncryptionApp.add("com.android.contacts");
    	mNotEncryptionApp.add("com.android.settings");
    }
    
}
