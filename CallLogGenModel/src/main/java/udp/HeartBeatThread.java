package udp;

import calllog.gen.main.PropertiesUtil;

import java.io.IOException;
import java.net.*;

/**
 * 工具类
 * 发送心跳信息，证明程序还活着
 * 监控使用
 */
public class HeartBeatThread extends Thread{

    private DatagramSocket sock;

    public HeartBeatThread()
    {
        try {
            sock = new DatagramSocket(PropertiesUtil.getInt("heartbeat.udp.send.port"));
            //守护线程
            this.setDaemon(true);
            System.out.println("开始发送心跳");

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] bs = new byte[1];
        bs[0] = (byte)PropertiesUtil.getInt("heartbeat.udp.send.flag");
        DatagramPacket packet = new DatagramPacket(bs,1);
        String bcAddr = PropertiesUtil.getString("heartbeat.udp.send.bcAddr");
        int bcPort = PropertiesUtil.getInt("heartbeat.udp.send.bcPort");
        packet.setSocketAddress(new InetSocketAddress(bcAddr,bcPort));
        while(true)
        {
            try {
                sock.send(packet);
                Thread.sleep(PropertiesUtil.getInt("heartbeat.udp.send.sleep.ms"));
                System.out.println("数据生成模块，发送1次心跳" + bs[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
