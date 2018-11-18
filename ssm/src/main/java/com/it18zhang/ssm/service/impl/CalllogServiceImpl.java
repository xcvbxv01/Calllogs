package com.it18zhang.ssm.service.impl;

import com.it18zhang.ssm.domain.Calllog;
import com.it18zhang.ssm.domain.CalllogRange;
import com.it18zhang.ssm.service.CalllogService;
import com.it18zhang.ssm.service.PersonService;
import com.it18zhang.ssm.util.CalllogUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * CalllogService的实现类
 */
@Service("calllogService")
public class CalllogServiceImpl implements CalllogService {

    @Resource(name = "personService")
    private PersonService ps;

    private Table table;
    public CalllogServiceImpl()
    {
        try {
            //获取配置文件
            Configuration conf = HBaseConfiguration.create();
            //工厂类创建连接
            Connection conn = ConnectionFactory.createConnection(conf);
            //get table
            TableName tbName = TableName.valueOf("call:calllogs");
            table = conn.getTable(tbName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有的calllog
     * 全表扫描
     * @return
     */
    public List<Calllog> findAll() {
        List<Calllog> list = new ArrayList<Calllog>();
        try {
            //扫描
            Scan scan = new Scan();
            ResultScanner rs = table.getScanner(scan);
            Iterator<Result> it = rs.iterator();
            byte[] famliy = Bytes.toBytes("f1");
            byte[] callerf = Bytes.toBytes("caller");
            byte[] calleef = Bytes.toBytes("callee");
            byte[] callTimef = Bytes.toBytes("callTime");
            byte[] callDurationf = Bytes.toBytes("callDuration");
            Calllog calllog = null;

            while (it.hasNext()) {

                Result next = it.next();
                String caller = Bytes.toString(next.getValue(famliy, callerf));
                String callee = Bytes.toString(next.getValue(famliy, calleef));
                String callTime = Bytes.toString(next.getValue(famliy, callTimef));
                String callDuration = Bytes.toString(next.getValue(famliy, callDurationf));

                calllog = new Calllog();
                //rowkey
                String rowkey = Bytes.toString(next.getRow());
                String flag = rowkey.split(",")[3];
                calllog.setFlag(flag.equals("0")?true:false);

                calllog.setCaller(caller);
                calllog.setCallee(callee);
                calllog.setCallTime(callTime);
                calllog.setCallDuration(callDuration);

                //查询mysql设置主叫名字和被叫名字
                String callerName = ps.selectNameByPhone(caller);
                String calleeName = ps.selectNameByPhone(callee);
                calllog.setCallerName(callerName);
                calllog.setCalleeName(calleeName);

                list.add(calllog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 查询hbase，获取callllogRange所对应的所有记录
     */
    public List<Calllog> findCallogs(String call , List<CalllogRange> ranges){
        List<Calllog> logs = new ArrayList<Calllog>();
        try {
            for(CalllogRange range : ranges){
                Scan scan = new Scan();

                //设置扫描起始行
                scan.setStartRow(Bytes.toBytes(CalllogUtil.getRowkey(call, range.getStartPoint(), 100)));
                //设置扫描结束行
                scan.setStopRow(Bytes.toBytes(CalllogUtil.getRowkey(call, range.getEndPoint(),100)));

                ResultScanner rs = table.getScanner(scan);
                Iterator<Result> it = rs.iterator();
                byte[] f = Bytes.toBytes("f1");

                byte[] caller = Bytes.toBytes("caller");
                byte[] callee = Bytes.toBytes("callee");
                byte[] callTime = Bytes.toBytes("callTime");
                byte[] callDuration = Bytes.toBytes("callDuration");
                Calllog log = null;
                while (it.hasNext()) {
                    log = new Calllog();
                    Result r = it.next();
                    //rowkey
                    String rowkey = Bytes.toString(r.getRow());
                    String flag = rowkey.split(",")[3];
                    log.setFlag(flag.equals("0")?true:false);
                    //caller
                    log.setCaller(Bytes.toString(r.getValue(f, caller)));
                    //callee
                    log.setCallee(Bytes.toString(r.getValue(f, callee)));
                    //callTime
                    log.setCallTime(Bytes.toString(r.getValue(f, callTime)));
                    //callDuration
                    log.setCallDuration(Bytes.toString(r.getValue(f, callDuration)));
                    logs.add(log);
                }
            }
            return logs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
