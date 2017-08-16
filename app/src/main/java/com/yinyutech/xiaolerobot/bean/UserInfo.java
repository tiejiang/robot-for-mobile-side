package com.yinyutech.xiaolerobot.bean;

import android.content.Context;

import com.yinyutech.xiaolerobot.utils.soundbox.PreferencesUtils;

import org.json.JSONObject;


/**
 * Created by kevin on 10/22/15.
 */
public class UserInfo {
    public String customid = "";
    public String username = "";
    public String password = "";
    public String mobile = "";
    public String email = "";
    public String registerip = "";
    public String registerdate = "";
    public String lastupdatelogin = "";

    private static UserInfo single = null;
    private Context context;

    public synchronized static UserInfo sharedUserInfo() {
        if (null == single) {
            single = new UserInfo();
        }
        return single;
    }

    public void setupSharedUserInfo(Context context) {
        this.context = context;

        loadSavedUserInfo();
    }

    public boolean isUserLogin() {
        return customid != null && customid.length() > 0;
    }

    public void logout() {
        updateLoginStatus(null);
    }

    public void login(JSONObject o) {
        updateLoginStatus(o);
    }

    private void updateLoginStatus(JSONObject o) {
        if (o == null)
            o = new JSONObject();

        customid = o.optString("custId");
        username = o.optString("username");
        password = o.optString("password");
        mobile = o.optString("mobile");
        email = o.optString("email");
        registerip = o.optString("regIp");
        registerdate = o.optString("regDate");
        lastupdatelogin = o.optString("lastUpdateDate");

        saveUserInfo();
    }

    private void loadSavedUserInfo() {
        customid = PreferencesUtils.getString(context, "customid", "");
        username = PreferencesUtils.getString(context, "username", "");
        password = PreferencesUtils.getString(context, "password", "");
        mobile = PreferencesUtils.getString(context, "mobile", "");
        email = PreferencesUtils.getString(context, "email", "");
        registerip = PreferencesUtils.getString(context, "registerip", "");
        registerdate = PreferencesUtils.getString(context, "registerdate", "");
        lastupdatelogin = PreferencesUtils.getString(context, "lastupdatelogin", "");
    }

    private void saveUserInfo() {
        PreferencesUtils.putString(context, "customid", customid);
        PreferencesUtils.putString(context, "username", username);
        PreferencesUtils.putString(context, "password", password);
        PreferencesUtils.putString(context, "mobile", mobile);
        PreferencesUtils.putString(context, "email", email);
        PreferencesUtils.putString(context, "registerip", registerip);
        PreferencesUtils.putString(context, "registerdate", registerdate);
        PreferencesUtils.putString(context, "lastupdatelogin", lastupdatelogin);
    }
}
