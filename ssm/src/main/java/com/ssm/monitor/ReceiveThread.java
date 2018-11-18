package com.ssm.monitor;

import com.it18zhang.ssm.domain.HeartBeat;
import com.it18zhang.ssm.util.PropertiesUtil;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiveThread extends Thread{

    private DatagramSocket sock;
    //ip + 最后一次收到心跳的时间戳
    public Map<String, HeartBeat> map = new HashMap<String, HeartBeat>();

    public ReceiveThread() {

        try{
            //设定接收端口
            sock = new DatagramSocket(PropertiesUtil.getInt("heartbeat.udp.receive.port"));
            //守护线程
            this.setDaemon(true);

            System.out.println("开始接收心跳 ...");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] bs = new byte[1];
        DatagramPacket packet = new DatagramPacket(bs,1);
        while(true)
        {
            try {
                sock.receive(packet);
                int flag = bs[0];
                InetSocketAddress addr = (InetSocketAddress) packet.getSocketAddress();
                String sendIp = addr.getAddress().getHostAddress();
                map.put(sendIp,new HeartBeat(sendIp, flag, System.currentTimeMillis()));
                System.out.println("接收心跳" + flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
