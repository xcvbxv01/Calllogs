package com.it18zhang.ssm.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.it18zhang.ssm.domain.Calllog;
import com.it18zhang.ssm.domain.CalllogRange;
import com.it18zhang.ssm.domain.CalllogStat;
import com.it18zhang.ssm.service.CalllogService;
import com.it18zhang.ssm.service.PersonService;
import com.it18zhang.ssm.service.impl.CalllogServiceImpl;
import com.it18zhang.ssm.util.CalllogUtil;
import com.ssm.callloghive.service.HiveService;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Controller
public class CalllogController {

    @Resource(name="calllogService")
    private CalllogService cs;

    @Resource
    private HiveService hcs;

    @Resource(name = "personService")
    private PersonService ps;

    /**
     * 发送参数
     * @param model
     * @return
     */
    @RequestMapping("calllog/findAll")
    public String findAll(Model model)
    {
        List<Calllog> list = cs.findAll();
        model.addAttribute("calllogs", list);
        return "calllog/calllogList";
    }

    /**
     * 模拟最底层的request和response,实现直接返回json串到前端页面
     */
    @RequestMapping("calllog/json/findAll")
    public String findAllJson(HttpServletResponse response)
    {
        try {
            List<Calllog> list = cs.findAll();
            String jsonStr = JSONArray.toJSONString(list);
            //设定回应的数据类型是json串
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            //得到发送给客户端的输出流
            ServletOutputStream sos = response.getOutputStream();
            sos.write(jsonStr.getBytes("utf-8"));
            sos.flush();
            sos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 跳转到查询界面
     * @return
     */
    @RequestMapping("calllog/toFindCalllogPage")
    public String toFindCalllog()
    {
        return "calllog/findCalllog";
    }

    /**
     * 接受参数
     * @param m
     * @return
     */
    @RequestMapping(value = "calllog/findCalllog", method = RequestMethod.POST)
    public String findCalllog(Model m, @RequestParam("caller") String caller, @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime)
    {
        //获取起止时间段的所有月份
        List<CalllogRange> list = CalllogUtil.getCalllogRanges(startTime, endTime);
        //查询hbase，获取callllogRange所对应的所有记录
        List<Calllog> logs = cs.findCallogs(caller,list);
        m.addAttribute("calllogs", logs);
        return "calllog/calllogList" ;
    }

    @RequestMapping(value = "calllog/toFindLatestCalllog")
    public String toFindLatestCalllogPage()
    {
        return "calllog/findLatestCalllog";
    }


    /**
     * 接受参数
     * @param m
     * @return
     */
    @RequestMapping(value = "calllog/findLatestCalllog", method = RequestMethod.POST)
    public String findLatestCalllog(Model m, @RequestParam("caller") String caller)
    {
        Calllog log = hcs.findLatestCallLog(caller);
        List<Calllog> logs = new ArrayList<Calllog>();
        logs.add(log);
        m.addAttribute("calllogs", logs);
        return "calllog/calllogList" ;
    }

    /**
     * 统计指定人员，指定月份的通话次数
     */
    @RequestMapping("/calllog/toStatCalllog")
    public String toStatCalllog(){
        return "calllog/statCalllog" ;
    }

    /**
     * 统计指定人员，指定月份的通话次数
     */
    @RequestMapping("/calllog/statCalllog")
    public String statCalllog(Model m ,@RequestParam("caller") String caller ,@RequestParam("year") String year){
        List<CalllogStat> list = hcs.statCalllogsCount(caller, year);

        m.addAttribute("title", caller + "在" + year + "年各月份通话次数统计");
        m.addAttribute("list", list);

        return "calllog/statCalllog" ;

    }
}
