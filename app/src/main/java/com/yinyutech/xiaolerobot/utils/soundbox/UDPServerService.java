package com.yinyutech.xiaolerobot.utils.soundbox;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.yinyutech.xiaolerobot.model.AddBoxDeviceReadyEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import de.greenrobot.event.EventBus;

public class UDPServerService extends Service {
    private final IBinder mBinder = new MyBinder();
    private DatagramSocket serverSocket;
    private android.os.Handler eventHandler = new android.os.Handler();

    private static final int appServerPort = 21239;

    public UDPServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            serverSocket = new DatagramSocket(appServerPort);
            new Thread(new UDPDatagramReceiver(serverSocket)).start();
        } catch (Exception e) {
            // do nothing
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        UDPServerService getService() {
            return UDPServerService.this;
        }
    }

    private class UDPDatagramReceiver implements Runnable{
        private static final int MAX_UDP_DATAGRAM_LEN = 1000;
        private DatagramSocket socket;

        public UDPDatagramReceiver(DatagramSocket socket) {
            this.socket = socket;
        }

        public void run() {
            byte[] udpMessage = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramPacket packet = new DatagramPacket(udpMessage, udpMessage.length);

            try {
                while(socket != null && !socket.isClosed()) {
                    socket.receive(packet);

                    if (packet.getLength() > 0) {
                        String message = new String(udpMessage, 0, packet.getLength());
                        Log.d("udptag", message);

                        try {
                            JSONObject json = new JSONObject(message);
                            String state = json.getString("state");
                            final String hostip = json.getString("hostip");
                            String name = json.getString("name");

                            if (state.equals("disconnect") && name.equals("HBL") && hostip != null) {
                                eventHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new AddBoxDeviceReadyEvent(hostip));
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("udptag", "empty packet");
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            Log.d("udptag", "UDP server shutdown");
        }
    }
}
