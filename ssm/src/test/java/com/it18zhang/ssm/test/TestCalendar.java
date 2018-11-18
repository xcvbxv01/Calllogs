package com.it18zhang.ssm.test;

import com.it18zhang.ssm.domain.CalllogRange;
import org.junit.Test;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TestCalendar {

    @Test
    public void test1() throws Exception {
        SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfYM = new SimpleDateFormat("yyyyMM");
        DecimalFormat df00 = new DecimalFormat("00") ;

        //获取起始和结束时间的calllogRange
        List<CalllogRange> list = new ArrayList<CalllogRange>();
        //起始字符串时间
        String startStr = "20140203" ;
        //取得起始年月字符串
        String startPrefix = startStr.substring(0,6);

        //结束日期
        String endStr = "20160304" ;
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

            System.out.println("");
        }
    }


}
