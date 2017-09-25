package com.yinyutech.xiaolerobot.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lezhi.soundbox.model.AddBoxButtonEnableEvent;
import com.lezhi.soundbox.model.AddBoxDeviceReadyEvent;
import com.lezhi.soundbox.util.BoxUDPBroadcaster;
import com.lezhi.soundbox.util.SoundBoxManager;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class AddBoxPairFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView pairTipTextView;
    private BoxUDPBroadcaster udpBroadcaster = new BoxUDPBroadcaster();
    private boolean isConnectingBoxDevice = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_box_pair, container, false);
        progressBar = (ProgressBar)v.findViewById(R.id.pairProgressBar);
        pairTipTextView = (TextView)v.findViewById(R.id.pairTipTextView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        SoundBoxManager.getInstance().isDiscoveringBox = true;

        progressBar.setVisibility(View.VISIBLE);
        pairTipTextView.setText("正在扫描音箱，请稍候...");
        // 通过eventBus发送消息
        EventBus.getDefault().post(new AddBoxButtonEnableEvent(false, false));

        EventBus.getDefault().register(this);
        udpBroadcaster.startBroadcastSearchBox();
    }

    @Override
    public void onPause() {
        super.onPause();

        SoundBoxManager.getInstance().isDiscoveringBox = false;

        EventBus.getDefault().unregister(this);
        udpBroadcaster.stopBroadcastSearchBox();
    }

    @Subscribe
    public void onEventMainThread(AddBoxDeviceReadyEvent event) {
        if (isConnectingBoxDevice || event.boxDeviceIP == null)
            return;

        // 停止广播，先尝试连接当前音箱
        isConnectingBoxDevice = true;
        udpBroadcaster.stopBroadcastSearchBox();

        SoundBoxManager manager = SoundBoxManager.getInstance();
        String boxHost = manager.boxHostFromIP(event.boxDeviceIP );

        manager.connectToBoxHost(boxHost, pairCompletion);
    }

    private SoundBoxManager.SoundBoxManagerCompletion pairCompletion = new SoundBoxManager.SoundBoxManagerCompletion() {
        @Override
        public void onFinish(boolean success) {
            Log.d("Pair Box Device", success ? "connectToBoxHost success" : "connectToBoxHost failed");

            EventBus.getDefault().post(new AddBoxButtonEnableEvent(success, success));

            // 如果连接音箱失败，则再次广播查找音箱
            if (!success) {
                isConnectingBoxDevice = false;
                udpBroadcaster.startBroadcastSearchBox();
            } else {
                Log.d("TIEJIANG", "AddBoxPairFragment---box is paired");
                showTipResult();

            }
        }
    };

    private void showTipResult() {
        // run on main thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                pairTipTextView.setText("音箱配对成功");
            }
        });
    }
}
