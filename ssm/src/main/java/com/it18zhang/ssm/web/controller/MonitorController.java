package com.it18zhang.ssm.web.controller;


import com.alibaba.fastjson.JSON;
import com.it18zhang.ssm.domain.HeartBeat;
import com.ssm.monitor.MonitorService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class MonitorController {

    @Resource(name="monitorService")
    private MonitorService ms;

    @RequestMapping("/monitor/toMonitorPage")
    public String toMonitorPage()
    {
        return "monitor/monitorPage";
    }

    @RequestMapping("/json/monitor/getMonitorInfo")
    public String getMonitorInfo(@RequestParam("heartbeat")HeartBeat ht)
    {
        List<HeartBeat> list = ms.getHeartBeats();
        return JSON.toJSONString(list);
    }

}
