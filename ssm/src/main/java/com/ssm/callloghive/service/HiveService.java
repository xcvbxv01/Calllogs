package com.ssm.callloghive.service;

import com.it18zhang.ssm.domain.Calllog;
import com.it18zhang.ssm.domain.CalllogStat;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Service
public class HiveService {
    //hiveserver2连接串
    private static String url = "jdbc:hive2://s100:10000/" ;
    //驱动程序类
    private static String driverClass = "org.apache.hive.jdbc.HiveDriver" ;

    static{
        try {
            Class.forName(driverClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 查询最近的通话记录,使用hive进行mr查询.
     */
    public Calllog findLatestCallLog(String phoneNum){

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement st = conn.createStatement();
            String sql = "select * from ext_calllogs_in_hbase where id like '%"+ phoneNum+"%' order by callTime desc limit 1" ;
            ResultSet rs = st.executeQuery(sql);
            Calllog log = null ;
            while(rs.next()){
                log = new Calllog();
                log.setCaller(rs.getString("caller"));
                log.setCallee(rs.getString("callee"));
                log.setCallTime(rs.getString("callTime"));
                log.setCallDuration(rs.getString("callDuration"));
            }
            rs.close();
            return log;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }

    /**
     * 查询指定人员指定年份中各个月份的通话次数
     */
    public List<CalllogStat> statCalllogsCount(String caller, String year){
        List<CalllogStat> list = new ArrayList<CalllogStat>() ;
        SparkConf conf = new SparkConf();
        conf.setAppName("SparkHive");
        conf.setMaster("spark://s100:7077,s500:7077");
        SparkSession sess = SparkSession.builder().config(conf).getOrCreate();
        SparkContext sc = sess.sparkContext();
        //拼串: select count(*) , substr(calltime,1,6) from ext_calllogs_in_hbase where caller = '15338597777'
        //      and substr(calltime,1,4) == '2018' group by substr(calltime,1,6) ;
        String sql = "select count(*) ,substr(calltime,1,6) from ext_calllogs_in_hbase " +
                "where caller = '" + caller+"' and substr(calltime,1,4) == '" + year
                + "' group by substr(calltime,1,6)";

        Dataset<Row> df =  sess.sql(sql);
        List<Row> lst = df.collectAsList();
        for(Row r : lst)
        {
            CalllogStat logSt = new CalllogStat();
            logSt.setCount(r.getInt(1));
            logSt.setYearMonth(r.getString(2));
            list.add(logSt);
        }
        return list;
    }


    /**
     * 查询指定人员指定年份中各个月份的通话次数
     */
    public List<CalllogStat> statCalllogsCount_1(String caller, String year){
        List<CalllogStat> list = new ArrayList<CalllogStat>() ;
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement st = conn.createStatement();
            //拼串: select count(*) , substr(calltime,1,6) from ext_calllogs_in_hbase where caller = '15338597777'
            //      and substr(calltime,1,4) == '2018' group by substr(calltime,1,6) ;
            String sql = "select count(*) ,substr(calltime,1,6) from ext_calllogs_in_hbase " +
                    "where caller = '" + caller+"' and substr(calltime,1,4) == '" + year
                    + "' group by substr(calltime,1,6)";
            ResultSet rs = st.executeQuery(sql);
            Calllog log = null;
            while (rs.next()) {
                CalllogStat logSt = new CalllogStat();
                logSt.setCount(rs.getInt(1));
                logSt.setYearMonth(rs.getString(2));
                list.add(logSt);
            }
            rs.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
