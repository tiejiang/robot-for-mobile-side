package com.yinyutech.xiaolerobot.utils.soundbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by yinyu-tiejiang on 17-8-28.
 * this class is used for sending local command only
 */

public class XiaoLeLocalSendingCommand {

    private static final int TIMEOUT = 5000;  //设置接收数据的超时时间
    private static final int MAXNUM = 1;      //设置重发数据的最多次数
    private SharedPreferences mGetYTXIDsp;
    private Context activityContextForLocalSendingUDP;
    public static XiaoLeLocalSendingCommand xiaoLeLocalSendingCommandInstance = null;
    int count = 0;

    public XiaoLeLocalSendingCommand(Context context){
        this.activityContextForLocalSendingUDP = context;
    }

    public XiaoLeLocalSendingCommand(){

    }

    public static XiaoLeLocalSendingCommand getInstance(){

        if (xiaoLeLocalSendingCommandInstance == null){
            xiaoLeLocalSendingCommandInstance = new XiaoLeLocalSendingCommand();
        }
        return xiaoLeLocalSendingCommandInstance;
    }

    public void startLocalSending(String command){

        //计数，连续收到５次指令后才启动线程发送消息(根据实际情况调整此参数)
        count += 1;
        if (count >= 2){
            Log.d("TIEJIANG", "XiaoLeLocalSendingCommand---startLocalSending " + "count= " + count);
            new Thread(new CommandSendingRunnable(command)).start();
            count = 0;
        }
    }

    // 调用此函数是为了联网模式的配对阶段－－－传输云通讯ＩＤ
    private String sendUDPCommand(String command){
        DatagramSocket ds = null;
        String str_receive = "";
//        String[] id = getYTXID();
        byte[] data = new byte[128];
        byte[] jsonData = new byte[128];
        String ip = SoundBoxManager.getInstance().currentIP();
        Log.d("TIEJIANG", "XiaoLeLocalSendingCommand---sendYTXUDPData" + " currentIP= " + ip);
        if (ip == null){
            return "0";
        }

        try{
            JSONObject json = new JSONObject();
            json.put("name", "XiaoleClient");
            json.put("Clientip", ip);
            json.put("ClientContent", command);
            json.put("wanted", "sendLocalControlCommand");
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
                    Log.d("TIEJIANG", "XiaoLeLocalSendingCommand---sendYTXUDPData" + "UDPClient---received data");
                    //接收从服务端发送回来的数据
                    ds.receive(dp_receive);
                    receivedResponse = true;
                }catch(InterruptedIOException e){
                    //如果接收数据时阻塞超时，重发并减少一次重发的次数
                    tries += 1;
                    Log.d("TIEJIANG", "XiaoLeLocalSendingCommand---sendYTXUDPData" + "Time out," + (MAXNUM - tries) + " more tries..." );
                }
            }
            if(receivedResponse) {
                str_receive = new String(dp_receive.getData(), 0, dp_receive.getLength());
                Log.d("TIEJIANG", "BoxUDPBroadcaster---sendYTXUDPData" + " str_receive= " + str_receive);
                //由于dp_receive在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
                //所以这里要将dp_receive的内部消息长度重新置为128
                dp_receive.setLength(128);
            }else{
                Log.d("TIEJIANG", "XiaoLeLocalSendingCommand---sendYTXUDPData"  + " No response -- give up.");
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

    class CommandSendingRunnable implements Runnable{

        String commandString;
        public CommandSendingRunnable(String command){
            this.commandString = command;
        }
        @Override
        public void run() {
            String mes = sendUDPCommand(commandString);
//            if (mes != null){
//                mScanXiaoLeHandler.obtainMessage(0, mes).sendToTarget();
//            }
        }
    }
}
