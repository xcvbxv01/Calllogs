package calllog.gen.main;

import udp.HeartBeatThread;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class App {

    public static Random random = new Random();
    //电话簿
    public static Map<String, String> callers = new HashMap<String, String>();
    //电话号码
    public static List<String> phoneNumbers = new ArrayList<String>();

    static{
        callers.put("15811111111", "史让");
        callers.put("18022222222", "赵嗄");
        callers.put("15133333333", "张锕 ");
        callers.put("13269364444", "王以");
        callers.put("15032295555", "张噢");
        callers.put("17731086666", "张类");
        callers.put("15338597777", "李平");
        callers.put("15733218888", "杜跑");
        callers.put("15614209999", "任阳");
        callers.put("15778421111", "梁鹏");
        callers.put("18641241111", "郭彤");
        callers.put("15732641111", "刘飞");
        callers.put("13341101111", "段星");
        callers.put("13560191111", "唐华");
        callers.put("18301581111", "杨谋");
        callers.put("13520401111", "温英");
        callers.put("18332561111", "朱宽");
        callers.put("18620191111", "刘宗");
        phoneNumbers.addAll(callers.keySet());

    }

    public static void main(String [] args)
    {
        genCallLog();
        //开启监控线程
        new HeartBeatThread().start();
    }

    /**
     * 生成通话日志
     */
    private static void genCallLog() {

        try {
            //文件写入器
            FileWriter fw = new FileWriter(PropertiesUtil.getString("log.file"), true);

            while (true) {

                //主叫
                String caller = phoneNumbers.get(random.nextInt(callers.size()));
                String callerName = callers.get(caller);
                //被叫 （！= 主叫）
                String callee = phoneNumbers.get(random.nextInt(callers.size()));
                while (callee.equals(caller)) {
                    callee = phoneNumbers.get(random.nextInt(callers.size()));
                }
                String calleeName = callers.get(callee);
                //通话时长(<10min)
                int duration = random.nextInt(PropertiesUtil.getInt("call.duration.max")) + 1;
                DecimalFormat df = new DecimalFormat();
                df.applyPattern(PropertiesUtil.getString("call.duration.format"));
                String dur = df.format(duration);

                //通话时间timeStr
                int year = PropertiesUtil.getInt("call.year");;
                int month = random.nextInt(12);
                int day = random.nextInt(29) + 1;
                int hour = random.nextInt(24);
                int min = random.nextInt(60);
                int sec = random.nextInt(60);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year,month,day,hour,min,sec);

                Date date = calendar.getTime();
                //如果时间超过今天就重新qushijian取时间.
                Date now = new Date();
                if (date.compareTo(now) > 0) {
                    continue ;
                }

                SimpleDateFormat dfs = new SimpleDateFormat();
                dfs.applyPattern(PropertiesUtil.getString("call.time.format"));
                String timeStr = dfs.format(date);

                //通话日志
                //String callLog = caller + "," + callerName + "," + callee + "," + calleeName + "," + timeStr + "," + dur;
                String callLog = caller + ","  + callee  + "," + timeStr + "," + dur;
                fw.write(callLog+ "\r\n");
                fw.flush();
                System.out.println("callLog: " +  callLog);

                Thread.sleep(PropertiesUtil.getInt("gen.data.interval.ms"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
