package com.yinyutech.xiaolerobot.net;

import android.util.Log;

import com.yinyutech.xiaolerobot.entrance.ImageDownloadInterface;
import com.yinyutech.xiaolerobot.fractory.ActivityInstance;
import com.yinyutech.xiaolerobot.helper.IMChattingHelper;
import com.yinyutech.xiaolerobot.utils.Constant;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import java.util.List;

import static com.yinyutech.xiaolerobot.ui.activity.VideoActivity.mVideoStateHandler;
import static com.yinyutech.xiaolerobot.ui.fragment.DeviceControlFragment.mWLANHandler;
import static com.yinyutech.xiaolerobot.ui.fragment.HomeFragment.mStateChangeHandler;

/**
 * Created by yinyu-tiejiang on 17-9-27.
 */

public class YTXCommunicate implements IMChattingHelper.OnMessageReportCallback  {

    private String[] ytxID = new String[2];
    private ImageDownloadInterface mImageDownloadInterface = null;
    private boolean isStartYTXHandshake = true;  //是否开启云通讯的"握手"线程
    public static YTXCommunicate mYTXCommunicate = null;
    public boolean isWLANOK = false;    //通过外网是否能够和小乐通信－－－即外网状态＋云通讯状态

    public YTXCommunicate(){

        //获得云通讯ｉｄ　先要设置receiver才能够发送消息！
        ytxID = ActivityInstance.mSplashActivityInstance.getYTXID();
        IMChattingHelper.setOnMessageReportCallback(this);
    }

    public static YTXCommunicate getYTXCommunicateInstance(){
        if (mYTXCommunicate == null){
            mYTXCommunicate = new YTXCommunicate();
        }
        return mYTXCommunicate;
    }

//    public String[] getYTXID(){
//
//        String[] YTXID = new String[2];
//        SharedPreferences sp = ActivityInstance.mSplashActivityInstance.getApplicationContext().getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
//        String mobileXiaoLe = sp.getString(Constant.XIAOLE_YTX_MOBILE, "0");
//        String H3XiaoLe = sp.getString(Constant.XIAOLE_YTX_H3, "1");
//        YTXID[0] = mobileXiaoLe;
//        YTXID[1] = H3XiaoLe;
//
//        return YTXID;
//    }

    public void getImageDowndURL(ImageDownloadInterface imageDownloadInterface){

        this.mImageDownloadInterface = imageDownloadInterface;
    }

    /**
     * 在DeviceControlFragment 初次启动的时候，class: YTXHandshakeRunnabel尚未启动
     * 因此，单独使用此方法供DeviceControlFragment首次启动的时候检测网络状况使用
     * 在应用进入/启动了此surfaceView之后停止此＂握手＂线程，启动正常的＂握手＂线程
     */
    public void YTXHandshake(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isStartYTXHandshake){
                    handleSendTextMessage(Constant.HAND_SHAKE);
                    //如果Ｈ３已经掉线，则不会回调此类的onPushMessage方法，故此处要手动设置isWLANOK为false
                    //如果有进入回调方法，则会自动改变isWLANOK的值．
                    isWLANOK = false;
                    Log.d("TIEJIANG", "YTXCommunicate---YTXHandshakeRunnabel");
                    try {
                        Log.d("TIEJIANG", "YTXCommunicate---StartYTXHandshake");
                        Thread.sleep(7000);
                        mWLANHandler.obtainMessage(0, "ytx_offline").sendToTarget();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * stop YTX hand thread when send command to H3
     *
     * */
    public void YTXHandshakeStop(){

        isStartYTXHandshake = false;
    }

    public void YTXHandshakeStart(){

        if (isStartYTXHandshake == false){
            isStartYTXHandshake = true;
            YTXHandshake();
        }
    }

    @Override
    public void onMessageReport(ECError error, ECMessage message) {

    }

    @Override
    public void onPushMessage(String sessionId, List<ECMessage> msgs) {
        int msgsSize = msgs.size();
        String message = "";
        String thumbnailFileUrl = null;
        String remoteUrl = null;

        for (int i = 0; i < msgsSize; i++){
            if (msgs.get(i).getType()== ECMessage.Type.IMAGE){
                ECImageMessageBody mECImageMessageBody = (ECImageMessageBody)msgs.get(i).getBody();

                thumbnailFileUrl = mECImageMessageBody.getThumbnailFileUrl(); //缩略图地址
                remoteUrl = mECImageMessageBody.getRemoteUrl(); //原图地址
                Log.d("TIEJIANG", "YTXCommunicate---onPushMessage"
                        +", thumbnailFileUrl= "+thumbnailFileUrl + ", remoteUrl= " + remoteUrl);
                if (mImageDownloadInterface != null){
                    mImageDownloadInterface.onImageDownload(remoteUrl);
                    mImageDownloadInterface.onImagethumbDownload(thumbnailFileUrl);
                }
            }else if (msgs.get(i).getType() == ECMessage.Type.TXT){
                message = ((ECTextMessageBody) msgs.get(i).getBody()).getMessage();
//                Log.d("TIEJIANG", "YTXCommunicate---onPushMessage" + "i :" + i + ", message = " + message);
                Log.d("TIEJIANG", "YTXCommunicate---onPushMessage" + ",sessionId :" + sessionId);
                analysisYTX(message);
            }
        }
    }

    //解析Ｈ３平台发送过来的ＩＭ信息
    private void analysisYTX(String message){

        Log.d("TIEJIANG", "YTXCommunicate---analysisYTX"+" message= " + message);
        if (message.contains(Constant.HAND_OK)){
            isWLANOK = true;
            if (mWLANHandler != null){
                mWLANHandler.obtainMessage(0, message).sendToTarget();
            }
        }else if (message.equals(Constant.ALREADY_MAX_VOLUME)){
            mStateChangeHandler.obtainMessage(3, "max_volume").sendToTarget();
        }else if (message.equals(Constant.ALREADY_MIN_VOLUME)){
            mStateChangeHandler.obtainMessage(4, "mix_volume").sendToTarget();
        }else if (message.equals(Constant.XIAOLE_CAMERA_IS_IN_USE)){
            mVideoStateHandler.obtainMessage(0, "camera_is_in_use").sendToTarget();
        } else {
            isWLANOK = false;
        }
    }


    /**
     * 处理文本发送方法事件通知
     * @param text
     */
    public void handleSendTextMessage(CharSequence text) {
        if(text == null) {
            return ;
        }
        if(text.toString().trim().length() <= 0) {
            //canotSendEmptyMessage();
            return ;
        }
        // 组建一个待发送的ECMessage
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
        // 设置消息接收者
        //msg.setTo(mRecipients);
        msg.setTo(ytxID[1]); // attenionthis number is not the login number! / modified by tiejiang
        ECTextMessageBody msgBody=null;
        Boolean isBQMMMessage=false;
        String emojiNames = null;
        //if(text.toString().contains(CCPChattingFooter2.TXT_MSGTYPE)&& text.toString().contains(CCPChattingFooter2.MSG_DATA)){
        //try {
        //JSONObject jsonObject = new JSONObject(text.toString());
        //String emojiType=jsonObject.getString(CCPChattingFooter2.TXT_MSGTYPE);
        //if(emojiType.equals(CCPChattingFooter2.EMOJITYPE) || emojiType.equals(CCPChattingFooter2.FACETYPE)){//说明是含有BQMM的表情
        //isBQMMMessage=true;
        //emojiNames=jsonObject.getString(CCPChattingFooter2.EMOJI_TEXT);
        //}
        //} catch (JSONException e) {
        //e.printStackTrace();
        //}
        //}
        if (isBQMMMessage) {
            msgBody = new ECTextMessageBody(emojiNames);
            msg.setBody(msgBody);
            msg.setUserData(text.toString());
        } else {
            // 创建一个文本消息体，并添加到消息对象中
            msgBody = new ECTextMessageBody(text.toString());
            msg.setBody(msgBody);
            Log.d("TIEJIANG", "YTXCommunicate---handleSendTextMessage" + ", txt = " + text);// add by tiejiang
        }

        //String[] at = mChattingFooter.getAtSomeBody();
        //msgBody.setAtMembers(at);
        //mChattingFooter.clearSomeBody();
        try {
            // 发送消息，该函数见上
            long rowId = -1;
            //if(mCustomerService) {
            //rowId = CustomerServiceHelper.sendMCMessage(msg);
            //} else {
            Log.d("TIEJIANG", "YTXCommunicate---handleSendTextMessage");// add by tiejiang
            rowId = IMChattingHelper.sendECMessage(msg);

            //}
            // 通知列表刷新
            //msg.setId(rowId);
            //notifyIMessageListView(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TIEJIANG", "YTXCommunicate---handleSendTextMessage-send failed");// add by tiejiang
        }
    }
}
