package test.hbasedao;

import calllog.kafka.hbase.customer.HbaseDao;
import org.junit.Test;

public class TestHbaseDao {

    @Test
    public void testDao()
    {
        HbaseDao dao = new HbaseDao();
        String log = "13560191111,18620191111,2018/09/04 08:24:57,252";
        dao.put(log);
    }

}
