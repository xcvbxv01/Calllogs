package com.it18zhang.ssm.util;

import com.it18zhang.ssm.domain.CalllogRange;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * calllog工具类
 */
public class CalllogUtil {


    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat sdfFriend = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    //格式化
    private static DecimalFormat df = new DecimalFormat();

    /**
     * 获取hash值,默认分区数100
     */
    public static String getHashcode(String caller, String callTime, int partitions) {
        int len = caller.length();
        //取出后四位电话号码
        String last4Code = caller.substring(len - 4);
        //取出时间单位,年份和月份.
        String mon = callTime.substring(0, 6);
        //
        int hashcode = (Integer.parseInt(mon) ^ Integer.parseInt(last4Code)) % partitions;
        return df.format(hashcode);
    }

    /**
     * 计算rowkey
     */
    public static String getRowkey(String caller,String time, int partitions){
        String hashcode = getHashcode(caller, time,partitions);
        return hashcode + "," + caller + "," + time ;
    }



    /**
     * 计算出起始时间到结束时间所经历的时间片段（按月）
     */
    public static List<CalllogRange> getCalllogRanges(String startStr, String endStr)
    {
        //获取起始和结束时间的calllogRange
        List<CalllogRange> list = new ArrayList<CalllogRange>();
        try{

            SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdfYM = new SimpleDateFormat("yyyyMM");
            DecimalFormat df00 = new DecimalFormat("00") ;

            //取得起始年月字符串
            String startPrefix = startStr.substring(0,6);

            //结束年月
            String endPrefix = endStr.substring(0,6);
            //结束的天数
            int endDay = Integer.parseInt(endStr.substring(6, 8));
            //结束点[考虑到每个月最后一天31号 + 1 = 32 号，而不能跨越月份] 20160305
            String endPoint = endPrefix + df00.format(endDay + 1) ;

            //日历对象
            Calendar c = Calendar.getInstance() ;

            //同年月[同一个年份同一个月份]
            if(startPrefix.equals(endPrefix)){
                CalllogRange range = new CalllogRange();
                range.setStartPoint(startStr);          //设置起始点

                range.setEndPoint(endPoint);            //设置结束点，day + 1
                list.add(range) ;
            }
            else{
                //1.起始月
                CalllogRange range = new CalllogRange();
                range.setStartPoint(startStr);

                //设置日历的时间对象
                c.setTime(sdfYMD.parse(startStr));
                c.add(Calendar.MONTH,1);
                range.setEndPoint(sdfYM.format(c.getTime()));
                list.add(range) ;

                //是否是最后一月
                while(true){
                    //到了结束月份
                    if(endStr.startsWith(sdfYM.format(c.getTime()))){
                        range = new CalllogRange();
                        range.setStartPoint(sdfYM.format(c.getTime()));
                        range.setEndPoint(endPoint);
                        list.add(range) ;
                        break ;
                    }
                    else{
                        range = new CalllogRange();
                        //起始时间
                        range.setStartPoint(sdfYM.format(c.getTime()));

                        //增加月份
                        c.add(Calendar.MONTH,1);
                        range.setEndPoint(sdfYM.format(c.getTime()));
                        list.add(range);
                    }
                }
                return list;
            }
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }
        return null;
    }


    /**
     * 对时间进行格式化
     */
    public static String formatDate(String timeStr){
        try {
            return sdfFriend.format(sdf.parse(timeStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null ;
    }

}
