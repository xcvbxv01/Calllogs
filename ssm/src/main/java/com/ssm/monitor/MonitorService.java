package com.ssm.monitor;

import com.it18zhang.ssm.domain.HeartBeat;
import com.it18zhang.ssm.util.PropertiesUtil;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * 监控其他程序心跳信息的类
 */

@Service("monitorService")
public class MonitorService extends Thread{

    private ReceiveThread t;
    public MonitorService()
    {
        //开启监控线程
        t = new ReceiveThread();
        t.start();
    }

    public List<HeartBeat> getHeartBeats()
    {
        return new ArrayList<HeartBeat>(t.map.values());
    }
}
