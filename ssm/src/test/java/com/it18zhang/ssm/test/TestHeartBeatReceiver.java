package com.it18zhang.ssm.test;

import com.ssm.monitor.MonitorService;
import org.junit.Test;

public class TestHeartBeatReceiver {

    @Test
    public void test1()
    {
        new MonitorService();
        while(true)
        {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
