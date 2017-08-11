package com.yinyutech.xiaolerobot.utils;

import java.util.Vector;

public class Constant {
    public static final String PREF_PASSWORD_VALUE = "input_password_value";
    public static final String PREF_EMAIL_VALUE = "input_email_value";
    public static final String ACTIVITY_RESULT = "result";
    
    public static final int REQUEST_CODE = 1;
    public static final int ERROE_NUM = 5;

    //用户注册信息的sharedPreference
    public static final String USER_MESSAGE = "user_data";
    public static final String USER_NUMBER = "user_number";
    public static final String USER_SERCURITY = "user_security";

    //WIFI信息
    public static final String WIFI_MESSAGE = "user_data";
    public static final String WIFI_NAME = "wifi_name";
    public static final String WIFI_PWD = "wifi_pwd";
    
    public static Vector<String> mNotEncryptionApp;
    public static void filter(){
    	mNotEncryptionApp = new Vector<String>();
        mNotEncryptionApp.add("com.android.contacts");
    	mNotEncryptionApp.add("com.android.settings");
    }
    
}
