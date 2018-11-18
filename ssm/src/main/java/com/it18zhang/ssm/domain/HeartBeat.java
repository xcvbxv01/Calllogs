package com.it18zhang.ssm.domain;

public class HeartBeat {

    //udp发送方的ip
    private String ip;
    //发送的消息内容
    private int flag;
    //最近一次接收到消息时的事件
    private long ts;

    public HeartBeat() {

    }

    public HeartBeat(String ip, int flag, long ts) {
        this.ip = ip;
        this.flag = flag;
        this.ts = ts;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}


