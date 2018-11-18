import calllog.kafka.hbase.customer.HbaseDao;
import calllog.kafka.hbase.customer.HeartBeatThread;
import org.junit.Test;

public class TestHeartBeatSend {

    @Test
    public void testSend() throws InterruptedException {
        new HeartBeatThread().start();
        while (true) {
            Thread.sleep(100000);
        }
    }

}
