package com.yinyutech.xiaolerobot.model;

/**
 * Created by kevin on 8/28/15.
 */
public class AddBoxStatus {
    public String uploadWiFiName = "";
    public String uploadWiFiPassword = "";

    private AddBoxStatus() {}
    private static AddBoxStatus mInstance = new AddBoxStatus();

    public static AddBoxStatus getInstance() {
        return mInstance;
    }

    public void initialAddBoxStatus() {
        uploadWiFiName = "";
        uploadWiFiPassword = "";
    }
}
