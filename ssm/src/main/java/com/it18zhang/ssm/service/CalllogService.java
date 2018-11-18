package com.it18zhang.ssm.service;


import com.it18zhang.ssm.domain.Calllog;
import com.it18zhang.ssm.domain.CalllogRange;

import java.util.List;

/**
 * Calllog的服务类 -- 用于定制与服务器交互的规则
 */
public interface CalllogService {

    //查询所有的calllog
    public List<Calllog> findAll();
    /**
     * 按照范围查询通话记录
     */
    public List<Calllog> findCallogs(String call,List<CalllogRange> list);
}
