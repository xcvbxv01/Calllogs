package com.it18zhang.ssm.test;

import com.ssm.callloghive.service.HiveService;
import org.junit.Test;

public class TestHive {

    @Test
    public void testHive()
    {
        HiveService hive = new HiveService();
        hive.findLatestCallLog("17731086666");
    }
}
