package com.yinyutech.xiaolerobot.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yinyutech.xiaolerobot.R;
import com.yinyutech.xiaolerobot.helper.VoIPCallHelper;
import com.yinyutech.xiaolerobot.layoutcontrol.ECCallControlUILayout;
import com.yinyutech.xiaolerobot.layoutcontrol.ECCallHeadUILayout;
import com.yinyutech.xiaolerobot.ui.activity.VideoActivity;
import com.yinyutech.xiaolerobot.utils.CallFailReason;
import com.yinyutech.xiaolerobot.utils.DemoUtils;
import com.yinyutech.xiaolerobot.utils.ECNotificationManager;
import com.yinyutech.xiaolerobot.utils.ECPreferenceSettings;
import com.yinyutech.xiaolerobot.utils.ECPreferences;
import com.yuntongxun.ecsdk.CameraCapability;
import com.yuntongxun.ecsdk.CameraInfo;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECVoIPCallManager;
import com.yuntongxun.ecsdk.ECVoIPSetupManager;
import com.yuntongxun.ecsdk.VideoRatio;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;
import com.yuntongxun.ecsdk.voip.video.ECCaptureView;
import com.yuntongxun.ecsdk.voip.video.ECOpenGlView;
import com.yuntongxun.ecsdk.voip.video.OnCameraInitListener;


/**
 * 备用ＵＩ方案
 * 准备添加ｖｉｄｅｏＡｃｔｉｖｉｔｙ到此ｆｒａｇｍｅｎｔ
 * 201708010
 * */
public class BackupHomeFragment extends Fragment
        implements VoIPCallHelper.OnCallEventNotifyListener , ECCallControlUILayout.OnCallControlDelegate
    , View.OnClickListener
    {

    private static  final  String TAG="HomeFragment";

    /**昵称*/
    public static final String EXTRA_CALL_NAME = "con.yuntongxun.ecdemo.VoIP_CALL_NAME";
    /**通话号码*/
    public static final String EXTRA_CALL_NUMBER = "con.yuntongxun.ecdemo.VoIP_CALL_NUMBER";
    /**呼入方或者呼出方*/
    public static final String EXTRA_OUTGOING_CALL = "con.yuntongxun.ecdemo.VoIP_OUTGOING_CALL";
    /**VoIP呼叫*/
    public static final String ACTION_VOICE_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VOICE_CALL";
    /**Video呼叫*/
    public static final String ACTION_VIDEO_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VIDEO_CALL";
    public static final String ACTION_CALLBACK_CALL = "con.yuntongxun.ecdemo.intent.ACTION_VIDEO_CALLBACK";

    /**通话昵称*/
    protected String mCallName;
    /**通话号码*/
    protected String mCallNumber;
    protected String mPhoneNumber;
    protected String mMeetingNo;
    protected int mMeetingType;
    boolean isConnect = false;
    /**是否来电*/
    protected boolean mIncomingCall = false;
    /**呼叫唯一标识号*/
    protected String mCallId;
    /**VoIP呼叫类型（音视频）*/
    protected ECVoIPCallManager.CallType mCallType;
    /**透传号码参数*/
    private static final String KEY_TEL = "tel";
    /**透传名称参数*/
    private static final String KEY_NAME = "nickname";
    private static final String KEY_CONFIG = "confid";
    private static final String KEY_CONFIG_TYPE = "conftype";
    private static final String KEY_CONFIG_SUD = "sud";
    protected ECCallHeadUILayout mCallHeaderView;
    protected ECCallControlUILayout mCallControlUIView;
    public AudioManager mAudioManager;
    private Intent sIntent;
    private Context mFragmentContext ;


    private static long lastClickTime;
    private Button mVideoStop;
    private Button mVideoBegin;
    private Button mVideoCancle;
    private ImageView mVideoIcon;
    private RelativeLayout mVideoTipsLy;
    private ImageView mDiaerpadBtn;
    public LinearLayout daiLayout;

    private TextView mVideoTopTips;
    private TextView mVideoCallTips;
    private TextView mCallStatus;
    private ECOpenGlView mRemoteView;
    private ECOpenGlView mSelfGlView;
    // Remote Video
    private FrameLayout mVideoLayout;
    private Chronometer mChronometer;

    private View mCameraSwitch;
    private View video_switch;
    private ECCaptureView mCaptureView;
    /**
     * 当前呼叫类型对应的布局
     */
    RelativeLayout mCallRoot;
    private boolean mMaxSizeRemote = true;
    public boolean isCreated = false;

    public View mFragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.ec_video_call,container,false);
//        fragmentView = inflater.inflate(R.layout.fragment_home,container,false);
        mAudioManager = ((AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE));
        mFragmentContext = getActivity();
//        if(base_init()) {
//            return ;
//        }

        if(mCallType == null) {
            mCallType = ECVoIPCallManager.CallType.VOICE;
        }
        initVideoLayout();
        isCreated = true;
        return mFragmentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        VoIPCallHelper.setOnCallEventNotifyListener(this);
        ECNotificationManager.cancelCCPNotification(ECNotificationManager.CCP_NOTIFICATOIN_ID_CALLING);

        if (mCallType == ECVoIPCallManager.CallType.VIDEO) {
            String ratio = ECPreferences
                    .getSharedPreferences()
                    .getString(
                            ECPreferenceSettings.SETTINGS_RATIO_CUSTOM.getId(),
                            (String) ECPreferenceSettings.SETTINGS_RATIO_CUSTOM
                                    .getDefaultValue());

            if (!TextUtils.isEmpty(ratio)) {
                String[] arr = ratio.split("\\*");
                int capIndex = getCampIndex(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                if (mCaptureView != null) {
                    mCaptureView.setLocalResolutionRatio(Integer.parseInt(arr[2]), capIndex);
                }
            } else {
                if (mCaptureView != null) {
                    mCaptureView.onResume();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        releaseWakeLock();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(VoIPCallHelper.isHoldingCall()) {
            ECNotificationManager.showCallingNotification(mCallType);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        VoIPCallHelper.mHandlerVideoCall = false;
        isCreated = false;
    }

    public void init() {

//        requestImages();

//        initRecyclerView();
    }

    private void initVideoLayout() {
        if (mIncomingCall) {
            // 来电
            mCallId = getActivity().getIntent().getStringExtra(ECDevice.CALLID);
            mCallNumber = getActivity().getIntent().getStringExtra(ECDevice.CALLER);
        } else {
            // 呼出
//            mCallName = getActivity().getIntent().getStringExtra(EXTRA_CALL_NAME);
//            mCallNumber = getActivity().getIntent().getStringExtra(EXTRA_CALL_NUMBER);
            //test code
            mCallName = getActivity().getIntent().getStringExtra("20170717");
            mCallNumber = getActivity().getIntent().getStringExtra("20170717");
        }

        initResourceRefs();


        setCaptureView(mCaptureView);
        attachGlView();
        if (!mIncomingCall) {
            mVideoTopTips.setText(R.string.ec_voip_call_connecting_server);
            mCallId = VoIPCallHelper.makeCall(mCallType, mCallNumber);
        } else {
            mVideoCancle.setVisibility(View.GONE);
            mVideoTipsLy.setVisibility(View.VISIBLE);
            mVideoBegin.setVisibility(View.VISIBLE);
            mVideoTopTips.setText((mCallName == null ? mCallNumber : mCallName) + getString(R.string.ec_voip_invited_video_tip));
            mVideoTopTips.setVisibility(View.VISIBLE);
        }

        if (mIncomingCall) {
            mVideoStop.setEnabled(true);
        }
    }
        public void setCaptureView(ECCaptureView captureView) {
            ECVoIPSetupManager setUpMgr = ECDevice.getECVoIPSetupManager();
            if (setUpMgr != null) {
                setUpMgr.setCaptureView(captureView);
            }
            addCaptureView(captureView);
        }

        /**
         * 添加预览到视频通话界面上
         *
         * @param captureView 预览界面
         */
        private void addCaptureView(ECCaptureView captureView) {
            if (mCallRoot != null && captureView != null) {
                mCallRoot.removeView(mCaptureView);
                mCaptureView = null;
                mCaptureView = captureView;
                mCallRoot.addView(captureView, new RelativeLayout.LayoutParams(1, 1));
                mCaptureView.setVisibility(View.VISIBLE);
//            LogUtil.d(TAG, "CaptureView added");
            }
        }

        private void attachGlView() {
            ECVoIPSetupManager setupManager = ECDevice.getECVoIPSetupManager();
            if(setupManager == null) {
                return ;
            }
            if(mMaxSizeRemote) {
                // 设置本地视频图像/远端视频图像显示窗口
                setupManager.setGlDisplayWindow(mSelfGlView , mRemoteView);
            } else {
                setupManager.setGlDisplayWindow(mRemoteView , mSelfGlView);
            }
        }

        private void initResourceRefs() {
            mCallRoot = (RelativeLayout)mFragmentView.findViewById(R.id.video_root);
            mVideoTipsLy = (RelativeLayout) mFragmentView.findViewById(R.id.video_call_in_ly);
            mVideoIcon = (ImageView) mFragmentView.findViewById(R.id.video_icon);

            mVideoTopTips = (TextView) mFragmentView.findViewById(R.id.notice_tips);
            mVideoCallTips = (TextView) mFragmentView.findViewById(R.id.video_call_tips);
            mVideoCancle = (Button) mFragmentView.findViewById(R.id.video_botton_cancle);
            mVideoBegin = (Button) mFragmentView.findViewById(R.id.video_botton_begin);
            mVideoStop = (Button) mFragmentView.findViewById(R.id.video_stop);
//        mDmfInput = (EditText) findViewById(R.id.dial_input_numer_TXT);
//        mDiaerpadBtn = (ImageView) findViewById(R.id.layout_call_dialnum);
//        mDiaerpadBtn.setOnClickListener(this);
//        daiLayout = (LinearLayout) findViewById(R.id.layout_dial_panel);

//        setupKeypad();

            mVideoStop.setEnabled(false);

            mVideoCancle.setOnClickListener(this);
            mVideoBegin.setOnClickListener(this);
            mVideoStop.setOnClickListener(this);
            // 远程图像显示配置
            mRemoteView = (ECOpenGlView) mFragmentView.findViewById(R.id.video_view); //远程图像显示
            mRemoteView.setVisibility(View.INVISIBLE);
            mRemoteView.setGlType(ECOpenGlView.RenderType.RENDER_REMOTE); //远端图像显示类型/绘制类型
            mRemoteView.setAspectMode(ECOpenGlView.AspectMode.CROP); // 图像等比按照中心区域显示屏截取
            // 本地图像显示配置
            mSelfGlView = (ECOpenGlView) mFragmentView.findViewById(R.id.localvideo_view);  //本地图像显示
            mSelfGlView.setGlType(ECOpenGlView.RenderType.RENDER_PREVIEW); //本地图像显示类型/绘制类型
            mSelfGlView.setAspectMode(ECOpenGlView.AspectMode.CROP); // 图像等比按照中心区域显示屏截取
            mSelfGlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaxSizeRemote = !mMaxSizeRemote;
                    attachGlView();//点击本地显示view时候交换view的显示内容
                }
            });


            mCaptureView = new ECCaptureView(getActivity());
            mCaptureView.setOnCameraInitListener(new OnCameraInitListener() {
                @Override
                public void onCameraInit(boolean result) {
                    if (!result) {
                        Log.d("TIEJIANG", "VideoActivity---initResourceRefs" + "摄像头被占用");
                        Toast.makeText(getActivity(), "摄像头被占用", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mVideoLayout = (FrameLayout) mFragmentView.findViewById(R.id.Video_layout);
            mCameraSwitch = mFragmentView.findViewById(R.id.camera_switch);
            mCameraSwitch.setOnClickListener(this);
            video_switch = mFragmentView.findViewById(R.id.video_switch);
            video_switch.setOnClickListener(this);

            mCallStatus = (TextView) mFragmentView.findViewById(R.id.call_status);
            mCallStatus.setVisibility(View.GONE);
        }

        private void initResVideoSuccess() {
            isConnect = true;
            mVideoLayout.setVisibility(View.VISIBLE);
            mVideoIcon.setVisibility(View.GONE);
            mVideoTopTips.setVisibility(View.GONE);
            mCameraSwitch.setVisibility(View.VISIBLE);
            mVideoTipsLy.setVisibility(View.VISIBLE);
            mVideoBegin.setVisibility(View.GONE);
            // bottom ...
            mVideoCancle.setVisibility(View.GONE);
            mVideoCallTips.setVisibility(View.VISIBLE);
//        mVideoCallTips.setText(getString(R.string.str_video_bottom_time, mCallNumber));
            mVideoCallTips.setText(R.string.str_video_bottom_time);
            mVideoStop.setVisibility(View.VISIBLE);
            mVideoStop.setEnabled(true);

            mCaptureView.setVisibility(View.VISIBLE);
            // mChronometer 计时器
            mChronometer = (Chronometer) mFragmentView.findViewById(R.id.chronometer);
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.setVisibility(View.VISIBLE);
            mChronometer.start();
            // mDiaerpadBtn.setVisibility(View.VISIBLE);
//        mDiaerpadBtn.setEnabled(false);
        }

        /**
         * 根据状态,修改按钮属性及关闭操作
         */
        private void finishCalling() {
            try {
                // mChronometer.setVisibility(View.GONE);
                mVideoTopTips.setVisibility(View.VISIBLE);
                mCameraSwitch.setVisibility(View.GONE);
                mVideoTopTips.setText(R.string.ec_voip_calling_finish);

                if (isConnect) {
                    // set Chronometer view gone..
                    mChronometer.stop();
                    mVideoLayout.setVisibility(View.GONE);
                    mVideoIcon.setVisibility(View.VISIBLE);
                    mCaptureView.setVisibility(View.GONE);
                    // bottom can't click ...
                    mVideoStop.setEnabled(false);
                } else {
                    mVideoCancle.setEnabled(false);
                }
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isConnect = false;
            }
        }

        private void finishCalling(int reason) {
            try {
                mVideoTopTips.setVisibility(View.VISIBLE);
                mCameraSwitch.setVisibility(View.GONE);
                mCaptureView.setVisibility(View.GONE);
//            mDiaerpadBtn.setVisibility(View.GONE);
                if (isConnect) {
                    mChronometer.stop();
                    mVideoLayout.setVisibility(View.GONE);
                    mVideoIcon.setVisibility(View.VISIBLE);
                    isConnect = false;
                    // bottom can't click ...
                    mVideoStop.setEnabled(false);
                } else {
                    mVideoCancle.setEnabled(false);
                }
                isConnect = false;
                mVideoTopTips.setText(CallFailReason.getCallFailReason(reason));
                VoIPCallHelper.releaseCall(mCallId);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private int getCampIndex(int width, int height, int index) {
            int sum = 0;
            ECVoIPSetupManager voIPSetupManager = ECDevice.getECVoIPSetupManager();
            if (voIPSetupManager == null) {
                return -1;
            }
            CameraInfo[] infos = voIPSetupManager.getCameraInfos();
            for (int i = 0; i < infos.length; i++) {

                CameraCapability[] arr = infos[i].caps;

                for (int j = 0; j < arr.length; j++) {

                    if (index == i && width == arr[j].width && height == arr[j].height) {
                        sum = j;
                    }
                }
            }
            return sum;
        }

    private boolean base_init() {
        sIntent = getActivity().getIntent();
        if(sIntent==null){
            return true;
        }
        sIntent = getActivity().getIntent();

        mIncomingCall = !(sIntent.getBooleanExtra(EXTRA_OUTGOING_CALL, false));
        mCallType = (ECVoIPCallManager.CallType) sIntent.getSerializableExtra(ECDevice.CALLTYPE);

        if(mIncomingCall) {
            // 透传信息
            String[] infos = sIntent.getExtras().getStringArray(ECDevice.REMOTE);
            if (infos != null && infos.length > 0) {
                for (String str : infos) {
                    if (str.startsWith(KEY_TEL)) {
                        mPhoneNumber = DemoUtils.getLastwords(str, "=");
                    } else if (str.startsWith(KEY_NAME)) {
                        mCallName = DemoUtils.getLastwords(str, "=");

                        // 如果有以下两个值说明是会议邀请来电
                    } else if(str.startsWith(KEY_CONFIG)) {
                        mMeetingNo = DemoUtils.getLastwords(str, "=");
                    } else if(str.startsWith(KEY_CONFIG_TYPE)) {
                        mMeetingType = Integer.parseInt(DemoUtils.getLastwords(str, "="));
                    }else if (str.startsWith(KEY_CONFIG_SUD)){
//                        LogUtil.d(TAG,"get invitemeeting sud = "+DemoUtils.getLastwords(str,"="));
                    }
                }
            }
        }

        if(!VoIPCallHelper.mHandlerVideoCall && mCallType == ECVoIPCallManager.CallType.VIDEO) {
            VoIPCallHelper.mHandlerVideoCall = true;
            Intent mVideoIntent = new Intent(getActivity() , VideoActivity.class);
            mVideoIntent.putExtras(sIntent.getExtras());
            mVideoIntent.putExtra(EXTRA_OUTGOING_CALL , false);
            startActivity(mVideoIntent);
//            super.finish();
            return true;
        }
        return false;

    }
    /**
     * 收到的VoIP通话事件通知是否与当前通话界面相符
     * @return 是否正在进行的VoIP通话
     */
    protected boolean isEqualsCall(String callId) {
        return (!TextUtils.isEmpty(callId) && callId.equals(mCallId));
    }

    /**
     * 是否需要做界面更新
     * @param callId
     * @return
     */
    protected boolean needNotify(String callId) {
//        return !(isFinishing() || !isEqualsCall(callId));
        return !(!isEqualsCall(callId));
    }

    @Override
    public void onCallProceeding(String callId) {
        if (callId != null && callId.equals(mCallId)) {
            mVideoTopTips.setText(getString(R.string.ec_voip_call_connect));
        }
    }

    @Override
    public void onCallAlerting(String callId) {
        if (callId != null && callId.equals(mCallId)) {// 等待对方接受邀请...
            mVideoTopTips.setText(getString(R.string.str_tips_wait_invited));
        }
    }

    @Override
    public void onCallAnswered(String callId) {  //答应通话，通话完全建立
        if (callId != null && callId.equals(mCallId) && !isConnect) {
            initResVideoSuccess();
            if (ECDevice.getECVoIPSetupManager() != null) {
                ECDevice.getECVoIPSetupManager().enableLoudSpeaker(true);
            }
        }
    }

    @Override
    public void onMakeCallFailed(String callId, int reason) {
        if (callId != null && callId.equals(mCallId)) {
            finishCalling(reason);
        }
    }

    @Override
    public void onCallReleased(String callId) {
        if (callId != null && callId.equals(mCallId)) {
            VoIPCallHelper.releaseMuteAndHandFree();
            finishCalling();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_botton_begin:
                VoIPCallHelper.acceptCall(mCallId);
                break;

            case R.id.video_stop:
            case R.id.video_botton_cancle:

                doHandUpReleaseCall();
                break;
            case R.id.camera_switch:
                mCameraSwitch.setEnabled(false);
                if (mCaptureView != null) {
                    mCaptureView.switchCamera();
                }
                mCameraSwitch.setEnabled(true);
                break;
            case R.id.layout_call_dialnum:
//                setDialerpadUI();
                break;
            default:
//                onKeyBordClick(v.getId());
                break;
        }
    }


    protected void doHandUpReleaseCall() {

        // Hang up the video call...
//        LogUtil.d(TAG,
//                "[VideoActivity] onClick: Voip talk hand up, CurrentCallId " + mCallId);
        try {
            if (mCallId != null) {

                if (mIncomingCall && !isConnect) {
                    VoIPCallHelper.rejectCall(mCallId);
                } else {
                    VoIPCallHelper.releaseCall(mCallId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isConnect) {
            finish();
        }
    }

    @Override
    public void onMakeCallback(ECError arg0, String arg1, String arg2) {

    }

//    public boolean isCreated = false;

//    @Override
//    protected void onNewIntent(Intent intent) {
//
//        if (!isCreated) {
//            setIntent(intent);
//            super.onNewIntent(intent);
//            initVideoLayout();
//        }
//    }

    /**
     * 远端视频分辨率到达，标识收到视频图像
     *
     * @param videoRatio 视频分辨率信息
     */
    @Override
    public void onVideoRatioChanged(VideoRatio videoRatio) {
//        super.onVideoRatioChanged(videoRatio);
    /*if(mVideoView != null && videoRatio != null) {
        mVideoView.getHolder().setFixedSize(videoRatio.getWidth() , videoRatio.getHeight());
    }*/
        if (videoRatio == null) {
            return;
        }
        int width = videoRatio.getWidth();
        int height = videoRatio.getHeight();
        if (width == 0 || height == 0) {
//            LogUtil.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
            Log.d("TIEJIANG", "VideoActivity---onVideoRatioChanged"
                    + "invalid video width(" + width + ") or height(" + height + ")");
            return;
        }
        mRemoteView.setVisibility(View.VISIBLE);
        if (width > height) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            int w = mSurfaceViewWidth * height / width;
            int margin = (mSurfaceViewHeight - mVideoTipsLy.getHeight() - w) / 2;
//            LogUtil.d(TAG, "margin:" + margin);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.setMargins(0, margin, 0, margin);
            mRemoteView.setLayoutParams(lp);
        }
    }

    public void setDialerpadUI() {
        daiLayout.setVisibility(daiLayout.getVisibility() != View.GONE ? View.GONE : View.VISIBLE);
    }



    @Override
    public void onViewAccept(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.acceptCall(mCallId);
        mCallControlUIView.setCallDirect(ECCallControlUILayout.CallLayout.INCALL);
        mCallHeaderView.setCallTextMsg(R.string.ec_voip_calling_accepting);

    }

    @Override
    public void onViewRelease(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.releaseCall(mCallId);
    }

    @Override
    public void onViewReject(ECCallControlUILayout controlPanelView, ImageButton view) {
        if(controlPanelView != null) {
            controlPanelView.setControlEnable(false);
        }
        VoIPCallHelper.rejectCall(mCallId);
    }

//    @Override
//    public void onVideoRatioChanged(VideoRatio videoRatio) {
//
//    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
////        LogUtil.e("setintent");
//        ECHandlerHelper.removeCallbacksRunnOnUI(OnCallFinish);
////        setIntent(intent);
////        setIntent(sIntent);
//        if(base_init()) {
//            return ;
//        }
//
//        if(mCallType == null) {
//            mCallType = ECVoIPCallManager.CallType.VOICE;
//        }
//    }

//    @Override
    public void finish() {
        ECHandlerHelper.postDelayedRunnOnUI(OnCallFinish , 3000);
    }
    public void hfFinish() {
        ECHandlerHelper.postDelayedRunnOnUI(OnCallFinish , 0);
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // 获取音频类型
//        int streamType = ECDevice.getECVoIPSetupManager().getStreamType();
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
//            // 调小音量
//            adjustStreamVolumeDown(streamType);
//            return true;
//        }
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
//            // 调大音量
//            adjustStreamVolumeUo(streamType);
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
    /**
     * 向下 调整音量
     * @param streamType 类型
     */
    public final void adjustStreamVolumeDown(int streamType) {
        if (this.mAudioManager != null)
            this.mAudioManager.adjustStreamVolume(streamType,AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    /**
     * 向上 调整音量
     * @param streamType 类型
     */
    public final void adjustStreamVolumeUo(int streamType) {
        if (this.mAudioManager != null)
            this.mAudioManager.adjustStreamVolume(streamType,AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    /**
     * 延时关闭界面
     */
    final Runnable OnCallFinish = new Runnable() {
        public void run() {
//            ECVoIPBaseActivity.super.finish();
        }
    };
}
