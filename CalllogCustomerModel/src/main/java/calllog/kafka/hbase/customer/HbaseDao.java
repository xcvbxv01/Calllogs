package calllog.kafka.hbase.customer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * hbase的数据访问对象
 */
public class HbaseDao {

    private Table table = null;
    private DecimalFormat df = new DecimalFormat();
    //设计rowkey的分区标识
    private int partitions;
    //0 -- 主叫  1 -- 被叫
    private String flag;

    public HbaseDao() {

        try {
            //获取配置文件
            Configuration conf = HBaseConfiguration.create();
            //工厂类创建连接
            Connection conn = ConnectionFactory.createConnection(conf);
            //get table
            TableName tbName = TableName.valueOf(PropertiesUtil.getPorp("table.name"));
            table = conn.getTable(tbName);
            df.applyPattern(PropertiesUtil.getPorp("hashcode.pattern"));
            partitions = Integer.parseInt(PropertiesUtil.getPorp("partition.number"));
            flag = PropertiesUtil.getPorp("caller.flag");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 向hbase中put数据
     */
    public void put(String log)
    {
        if(log == null || log.equals(""))
        {
            return;
        }

        try {
            //设计rowkey
            String rowKey = "";
            //解析日志
            String [] strs = log.split(",");
            if(strs != null && strs.length == 4)
            {
                String caller = strs[0];
                String callee = strs[1];
                String time = strs[2];
                time = time.replace("/", "");
                time = time.replace(" ", "");
                time = time.replace(":", "");
                String duration = strs[3];
                //计算区域号
                String hash = getRegionNumber(caller, time);
                rowKey = getRowkey(hash,caller,time,flag,callee,duration);

                //开始put
                Put p = new Put(Bytes.toBytes(rowKey));
                p.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("caller"),Bytes.toBytes(caller));
                p.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("callee"),Bytes.toBytes(callee));
                p.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("callTime"),Bytes.toBytes(time));
                p.addColumn(Bytes.toBytes("f1"), Bytes.toBytes("callDuration"),Bytes.toBytes(duration));
                table.put(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取区域号码 -- Rowkey设计用
     * @return
     */
    public String getRegionNumber(String caller, String calltime)
    {
        //取得电话号码的后四位
        String last4Code = caller.substring(caller.length() - 4);
        //取得通话时间的年月
        String month = calltime.substring(0, 6);
        int hash = (Integer.parseInt(last4Code) ^ Integer.parseInt(month)) % partitions;
        return df.format(hash);
    }

    /**
     * 获取rowkey
     */
    public String getRowkey(String hash, String caller,String time,String flag, String callee,String dur)
    {
        return hash + "," + caller + "," + time + "," + flag + "," + callee + "," + dur;
    }

}
