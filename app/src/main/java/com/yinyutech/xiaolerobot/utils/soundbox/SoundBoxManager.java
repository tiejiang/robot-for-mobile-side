package com.yinyutech.xiaolerobot.utils.soundbox;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yinyutech.xiaolerobot.bean.UserInfo;
import com.yinyutech.xiaolerobot.logger.Logger;
import com.yinyutech.xiaolerobot.model.AddBoxStatus;
import com.yinyutech.xiaolerobot.model.BoxConnectionChangedEvent;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * Created by kevin on 8/25/15.
 */
public class SoundBoxManager {
    public static final String kBoxWiFiHotspotName = "smartbox";
    public static final String kBoxWiFiHotspotPassword = "12345678";
    public static final String kBoxWiFiHotspotIP = "192.168.43.1";
    public static final int kBoxWiFiHotspotPort = 6188;

    public static final String kExternalIPServer1 = "http://city.ip138.com/ip2city.asp";
    public static final String kExternalIPServer2 = "http://www.whereismyip.com/";
    public String externalIP = "0.0.0.0";

    public static final String kLastConnectedBoxHostKey = "kLastConnectedBoxHostKey";

    // 音箱是否已经连接
    private boolean boxConnected = false;
    public boolean isDiscoveringBox = false;

    public boolean isBoxConnected() {
        return boxConnected;
    }

    public void setBoxConnected(boolean boxConnected) {
        this.boxConnected = boxConnected;

        EventBus.getDefault().post(new BoxConnectionChangedEvent());
    }

    // 只需要维持一个音箱
    public String getBoxHost() {
        return PreferencesUtils.getString(context, kLastConnectedBoxHostKey);
    }

    public void setBoxHost(String boxHost) {
        if (boxHost != null)
            PreferencesUtils.putString(context, kLastConnectedBoxHostKey, boxHost);
    }

    public interface SoundBoxManagerCompletion {
        void onFinish(boolean success);
    }

    public void connectToBoxHost(final String boxHost, final SoundBoxManagerCompletion completion) {
        if (boxHost == null) {
            if (completion != null)
                completion.onFinish(false);

            return;
        }

        setBoxHost(boxHost);

        // 用户已经登录，并且尚未连接音箱
        if (UserInfo.sharedUserInfo().isUserLogin() && !isBoxConnected()) {
            final SoundBoxServiceAction service = SoundBoxServiceAction.getInstance();
            service.signIn(new SoundBoxServiceAction.SoundBoxServiceActionListener() {
                @Override
                public void onFinish(boolean status, JSONObject responseJSONObj, boolean isNetworkingFailure) {
                    setBoxConnected(status);

                    if (status) {
                        // 连接音箱成功
//                        service.startQueryMusicPlayingInfo();
                    }

                    if (completion != null)
                        completion.onFinish(status);
                }
            });
        } else {
            if (completion != null)
                completion.onFinish(false);
        }
    }

    public void connectToLastBoxHost(final SoundBoxManagerCompletion completion) {
        String lastBoxHost = getBoxHost();

        if (lastBoxHost != null) {
            connectToBoxHost(lastBoxHost, completion);
        }
    }

    public boolean isWiFiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public String currentSSIDName() {
        String ssid = null;

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            ssid = wifiInfo.getSSID().replace("\"", "");
        }

//        if (WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState()) == NetworkInfo.DetailedState.CONNECTED) {
//            ssid = wifiInfo.getSSID();
//        }

        return ssid;
    }

    public String currentIP() {
        try {
            WifiManager wifiMgr = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            return Formatter.formatIpAddress(ip);
        } catch (Exception ex) {
        }
        
        return null;
    }

    public String boxHostFromIP(String ip) {
        return String.format("http://%s:8088/api", ip);
    }

    public void sendWifiInfoToBoxHotspot(SendWifiCompletion completion) {
        MyClientTask myClientTask = new MyClientTask(kBoxWiFiHotspotIP, kBoxWiFiHotspotPort, completion);
        myClientTask.execute();
    }

    public interface SendWifiCompletion {
        public void onFinish(boolean isSuccess);
    }

    private class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        SendWifiCompletion completion;

        MyClientTask(String addr, int port, SendWifiCompletion completion){
            dstAddress = addr;
            dstPort = port;
            this.completion = completion;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            OutputStream outputStream = null;
            boolean isSuccess = false;

            try {
                socket = new Socket("192.168.43.1", 6188);
                outputStream = socket.getOutputStream();

                JSONObject json = new JSONObject();
                json.put("sign", "940");
                json.put("ssid", AddBoxStatus.getInstance().uploadWiFiName);
                json.put("password", AddBoxStatus.getInstance().uploadWiFiPassword);

                String jsonString = json.toString();
                Logger.v("Wi-Fi json: %s", jsonString);
                outputStream.write(jsonString.getBytes("utf-8"));
                isSuccess = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            final boolean isSuccessFinal = isSuccess;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (completion != null) {
                        completion.onFinish(isSuccessFinal);
                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private Context context;

    private SoundBoxManager() {
    }

    public void setupContext(Context context) {
        if (context != null)
            this.context = context;

        getExternalIPAddressFromServer(kExternalIPServer1);
        getExternalIPAddressFromServer(kExternalIPServer2);
    }

    private static SoundBoxManager single;

    public synchronized static SoundBoxManager getInstance() {
        if (null == single) {
            single = new SoundBoxManager();
        }
        return single;
    }

    private void getExternalIPAddressFromServer(String server) {
        SoundBoxHttpClient.get(server, true, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String data = null;
                try {
                    data = new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (data != null) {
                    String regex = "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
//                    String ip = UiUtils.firstMatchString(regex, data);
                    String ip = firstMatchString(regex, data);  //modified by tiejiang
                    if (ip != null)
                        externalIP = ip;
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public static String firstMatchString(String regex,String mobiles) {

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(mobiles);
        if (m.find())
            return m.group();
        else
            return null;
    }
}
