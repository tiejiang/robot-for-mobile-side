package com.yinyutech.xiaolerobot.utils;

import java.util.Vector;

public class Constant {

    //云通讯ｋｅｙ
//    public static final String appKey = "8aaf070858cd982e0158e21ff0000cee";
//    public static final String token = "ca8bdec6e6ed3cc369b8122a1c19306d";
    public static final String appKey = "8a216da85e0e48b2015e1c039ba4056e";
    public static final String token = "4b07cb81f3a95ba0c183181ff04cb394";


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

    //通过云通讯发送到Ｈ３的握手信号
    public static final String HAND_SHAKE = "YTXHandshake";
    //收到Ｈ３的握手反馈信号
    public static final String HAND_OK = "handed";
    
    public static Vector<String> mNotEncryptionApp;


    //移动端和小乐通过ＩＭ通信指令－底盘控制
    public static final String BEGING_CONFIRM = "begin_confirm";  //移动端收到H3回发的确认开始发送指令
    public static final String BEGING_SEND = "query_begin";  //移动端请求开始发送运动控制指令
    public static final String MOBILE_FORWARD = "mobile_forward";
    public static final String MOBILE_BACK = "mobile_back";
    public static final String MOBILE_TURN_LEFT = "mobile_turn_left";
    public static final String MOBILE_TURN_RIGHTT = "mobile_turn_right";
    //移动端和小乐通过ＩＭ通信指令－头部控制
    public static final String MOBILE_TURN_HEAD_UP = "mobile_turn_up";
    public static final String MOBILE_TURN_HEAD_DOWN = "mobile_turn_down";
    public static final String MOBILE_TURN_HEAD_LEFT = "mobile_turn_head_left";
    public static final String MOBILE_TURN_HEAD_RIGHT = "mobile_turn_head_right";

    //移动端通过局域网和小乐进行通讯指令
//    public static final String LOCAL_NET_MOBILE_FORWARD = "local_net_mobile_forward";
//    public static final String LOCAL_NET_MOBILE_BACK = "local_net_mobile_back";
//    public static final String LOCAL_NET_MOBILE_TURN_LEFT = "local_net_mobile_turn_left";
//    public static final String LOCAL_NET_MOBILE_TURN_RIGHTT = "local_net_mobile_turn_right";


    public static void filter(){
    	mNotEncryptionApp = new Vector<String>();
        mNotEncryptionApp.add("com.android.contacts");
    	mNotEncryptionApp.add("com.android.settings");
    }
    
}
