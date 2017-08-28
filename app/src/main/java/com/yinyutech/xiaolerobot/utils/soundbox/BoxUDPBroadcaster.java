package com.yinyutech.xiaolerobot.utils.soundbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.yinyutech.xiaolerobot.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static com.yinyutech.xiaolerobot.ui.fragment.DeviceControlFragment.mScanXiaoLeHandler;

/**
 * Created by kevin on 9/19/15.
 */
public class BoxUDPBroadcaster {

    private static final int TIMEOUT = 1000;  //设置接收数据的超时时间
    private static final int MAXNUM = 2;      //设置重发数据的最多次数
    private SharedPreferences mGetYTXIDsp;
    private Context activityContext;
    public static DatagramSocket ds = null;

    public BoxUDPBroadcaster(Context context){
        this.activityContext = context;
    }

    public BoxUDPBroadcaster(){

    }

    public String[] getYTXID(){

        String[] ytxID = new String [2];
        SharedPreferences mGetYTXIDsp = activityContext.getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
        ytxID[0] = mGetYTXIDsp.getString(Constant.XIAOLE_YTX_MOBILE, "0");
        ytxID[1] = mGetYTXIDsp.getString(Constant.XIAOLE_YTX_H3, "1");

        return ytxID;
    }

    public void startBroadcastSearchBox() {
        handler.post(broadcaster);
        Log.d("TIEJIANG", "BoxUDPBroadcaster---startBroadcastSearchBox "+"handler= " + handler);
        Log.d("TIEJIANG", "BoxUDPBroadcaster---startBroadcastSearchBox "+"broadcaster= " + broadcaster);
    }

    public void stopBroadcastSearchBox() {
        handler.removeCallbacks(broadcaster);
        Log.d("TIEJIANG", "BoxUDPBroadcaster---stopBroadcastSearchBox");
    }

    public static DatagramSocket getSocketPort(){

        if (ds == null){
            try{
                ds = new DatagramSocket(21240);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return ds;
    }

    private String sendUDPDatagram(){
//        private static final int MAX_UDP_DATAGRAM_LEN = 1000;
//        Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram");
//        DatagramSocket ds = null;
        String str_receive = "";
        byte[] data = new byte[128];
        byte[] jsonData = new byte[128];
        String ip = SoundBoxManager.getInstance().currentIP();
        Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + "  currentIP= " + ip);
        if (ip == null){
            return "0";
        }
        // 调用此函数是与Ｈ３平台周期握手
        try{
            JSONObject json = new JSONObject();
            json.put("name", "XiaoleClient");
            json.put("Clientip", ip);
            json.put("ClientContent", "0");
            json.put("wanted", "search");

            String jsonString = json.toString();
            jsonData = jsonString.getBytes();

        }catch (JSONException e){
            e.printStackTrace();

        }
        try {

            ds = getSocketPort();

            InetAddress loc = InetAddress.getByName("255.255.255.255"); //广播出去，局域网内任何设备均收到
            //定义用来发送数据的DatagramPacket实例
            DatagramPacket dp_send= new DatagramPacket(jsonData,jsonData.length,loc,21230);
            //定义用来接收数据的DatagramPacket实例
            DatagramPacket dp_receive = new DatagramPacket(data, 128);
            //数据发向本地3000端口
            ds.setSoTimeout(TIMEOUT);              //设置接收数据时阻塞的最长时间
//            int tries = 0;                         //重发数据的次数
            boolean receivedResponse = false;     //是否接收到数据的标志位
//            Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + " receiver.address= " + receAddress);

//            while(!receivedResponse && tries<MAXNUM){
                //发送数据
                ds.send(dp_send);
                try{
                    Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + " UDPClient---received data");
                    //接收从服务端发送回来的数据
                    ds.receive(dp_receive);
                    //如果接收到的数据不是来自目标地址，则抛出异常
//                if(!dp_receive.getAddress().equals(loc)){
//                    throw new IOException("Received packet from an umknown source");
//                }
                    //如果接收到数据。则将receivedResponse标志位改为true，从而退出循环
                    receivedResponse = true;
                }catch(InterruptedIOException e){
                    e.printStackTrace();
                    //如果接收数据时阻塞超时，重发并减少一次重发的次数
//                    tries += 1;
//                    Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + "Time out," + (MAXNUM - tries) + " more tries..." );
                }
//            }
            if(receivedResponse) {
                //如果收到数据，则打印出来
//                Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + " client received data from server：");
//                String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength()) +
//                        " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
                str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
                Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + " str_receive= " + str_receive);

                // test code begin
//                JSONObject parseH3json = new JSONObject(str_receive);
//                String state = parseH3json.getString("state");
//                final String hostip = parseH3json.getString("hostip");
//                String name = parseH3json.getString("name");
//                String show = parseH3json.getString("show");
//                Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram" + " state= " + state + ", hostip= " + hostip + ", name= " + name + ", show= " + show);
//
//                if (state.equals("disconnect") && name.equals("HBL") && hostip != null) {
//
//                }
                // test code end

                //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
                //所以这里要将dp_receive的内部消息长度重新置为128
                dp_receive.setLength(128);
            }else{
                Log.d("TIEJIANG", "BoxUDPBroadcaster---sendUDPDatagram"  + " No response -- give up.");
                str_receive = "0";

            }
//            ds.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            if (ds != null){
//                ds.close();
//            }

        }
        return str_receive;
    }

    private static Handler handler = new Handler();
    Runnable broadcaster = new Runnable() {
        @Override
        public void run() {
            // 必须在线程里发送UDP广播
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    sendUDPDatagram();
                    String mes = sendUDPDatagram();
                    Log.d("TIEJIANG", "BoxUDPBroadcaster---broadcaster " + "mes= " + mes);
                    if (mes != null){
                        mScanXiaoLeHandler.obtainMessage(0, mes).sendToTarget();
                    }

                    handler.postDelayed(broadcaster, 3000);
                }
            }).start();
        }
    };
}
