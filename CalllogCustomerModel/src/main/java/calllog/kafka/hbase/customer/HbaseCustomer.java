package calllog.kafka.hbase.customer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.message.MessageAndMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.Properties;

/**
 * hbase消费者，从kafka获取日志信息，存储到hbase中
 */
public class HbaseCustomer {


    public static void main(String [] args)
    {
        //开启心跳发送
        new HeartBeatThread().start();

        //hbasedao
        HbaseDao dao = new HbaseDao();
        //创建消费者配置文件
        ConsumerConfig config = new ConsumerConfig(PropertiesUtil.props);
        //创建消费者
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(PropertiesUtil.props));
        //绑定主题
        String topic = PropertiesUtil.getPorp("topic");

        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(topic, new Integer(1));

        //开始消费
        Map<String, List<KafkaStream<byte[], byte[]>>> kafkaMsg = consumer.createMessageStreams(map);
        List<KafkaStream<byte[], byte[]>> msgList = kafkaMsg.get(topic);

        String kafka_hbaseMsg = "";
        for(KafkaStream<byte[],byte[]> msg : msgList)
        {
            ConsumerIterator<byte[],byte[]> mm = msg.iterator();
            while (mm.hasNext()) {
                MessageAndMetadata<byte[], byte[]> next = mm.next();
                byte [] m = next.message();
                //获取消息
                kafka_hbaseMsg = new String(m);
                //写入hbase
                dao.put(kafka_hbaseMsg);
            }
        }
    }
}
