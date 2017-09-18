package com.yinyutech.xiaolerobot.utils.soundbox;

import android.content.Context;
import android.content.SharedPreferences;
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

import static com.yinyutech.xiaolerobot.ui.fragment.DeviceControlFragment.mYTXIDSendCallbackHandler;

/**
 * Created by yinyu-tiejiang on 17-8-24.
 * XiaoLeUDP:负责联网步骤时候发送和配对云通讯ＩＤ
 */

public class XiaoLeUDP {

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 1;      //设置重发数据的最多次数
    private SharedPreferences mGetYTXIDsp;
    private Context activityContextForXiaoLeUDP;

    public XiaoLeUDP(Context context){
        this.activityContextForXiaoLeUDP = context;
    }

    public XiaoLeUDP(){

    }

    public void startXiaoLeUDP(){
        new Thread(new ScanXiaoLeRunnable()).start();
    }

    public String[] getYTXID(){

        String[] ytxID = new String [2];
        SharedPreferences mGetYTXIDsp = activityContextForXiaoLeUDP.getSharedPreferences(Constant.USER_MESSAGE, Context.MODE_PRIVATE);
        ytxID[0] = mGetYTXIDsp.getString(Constant.XIAOLE_YTX_MOBILE, "0");
        ytxID[1] = mGetYTXIDsp.getString(Constant.XIAOLE_YTX_H3, "1");

        return ytxID;
    }

    // 调用此函数是为了联网模式的配对阶段－－－传输云通讯ＩＤ
    private String sendYTXUDPData(){
//        private static final int MAX_UDP_DATAGRAM_LEN = 1000;
//        Log.d("TIEJIANG", "XiaoLeUDP---sendYTXUDPData");
        DatagramSocket ds = null;
        String str_receive = "";
        String[] id = getYTXID();
        byte[] data = new byte[128];
        byte[] jsonData = new byte[128];
        String ip = SoundBoxManager.getInstance().currentIP();
        Log.d("TIEJIANG", "XiaoLeUDP---sendYTXUDPData" + " currentIP= " + ip);
        if (ip == null){
            return "0";
        }

        try{
            JSONObject json = new JSONObject();
            json.put("name", "XiaoleClient");
            json.put("Clientip", ip);
            json.put("ClientContent", id[0]+","+id[1]);
            json.put("wanted", "sendYTXID");

            String jsonString = json.toString();
            jsonData = jsonString.getBytes();

        }catch (JSONException e){
            e.printStackTrace();

        }

        try {

            ds = BoxUDPBroadcaster.getSocketPort();
            InetAddress loc = InetAddress.getByName("255.255.255.255"); //广播出去，局域网内任何设备均收到
            //定义用来发送数据的DatagramPacket实例
            DatagramPacket dp_send= new DatagramPacket(jsonData,jsonData.length,loc,21230);
            //定义用来接收数据的DatagramPacket实例
            DatagramPacket dp_receive = new DatagramPacket(data, 128);
            //数据发向本地3000端口
            ds.setSoTimeout(TIMEOUT);              //设置接收数据时阻塞的最长时间
            int tries = 0;                         //重发数据的次数
            boolean receivedResponse = false;     //是否接收到数据的标志位
//            Log.d("TIEJIANG", "BoxUDPBroadcaster---sendYTXUDPData" + " receiver.address= " + receAddress);
            while(!receivedResponse && tries<MAXNUM){
                //发送数据
                ds.send(dp_send);
                try{
                    Log.d("TIEJIANG", "XiaoLeUDP---sendYTXUDPData" + "UDPClient---received data");
                    //接收从服务端发送回来的数据
                    ds.receive(dp_receive);
                    receivedResponse = true;
                }catch(InterruptedIOException e){
                    //如果接收数据时阻塞超时，重发并减少一次重发的次数
                    tries += 1;
                    Log.d("TIEJIANG", "XiaoLeUDP---sendYTXUDPData" + "Time out," + (MAXNUM - tries) + " more tries..." );
                }
            }
            if(receivedResponse) {
                //如果收到数据，则打印出来
//                Log.d("TIEJIANG", "XiaoLeUDP---sendYTXUDPData" + " client received data from server：");
//                String str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength()) +
//                        " from " + dp_receive.getAddress().getHostAddress() + ":" + dp_receive.getPort();
                str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
                Log.d("TIEJIANG", "BoxUDPBroadcaster---sendYTXUDPData" + " str_receive= " + str_receive);

                //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
                //所以这里要将dp_receive的内部消息长度重新置为128
                dp_receive.setLength(128);
            }else{
                Log.d("TIEJIANG", "XiaoLeUDP---sendYTXUDPData"  + " No response -- give up.");
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

    class ScanXiaoLeRunnable implements Runnable{

        @Override
        public void run() {
            String mes = sendYTXUDPData();
            Log.d("TIEJIANG", "XiaoLeUDP---ScanXiaoLeRunnable " + "mes= " + mes);
            if (mes != null){
                mYTXIDSendCallbackHandler.obtainMessage(0, mes).sendToTarget();
            }
        }
    }
}
