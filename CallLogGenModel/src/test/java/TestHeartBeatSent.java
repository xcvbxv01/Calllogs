import org.junit.Test;
import udp.HeartBeatThread;

public class TestHeartBeatSent {

    @Test
    public void test1()
    {
        new HeartBeatThread().start();
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
