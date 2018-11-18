package calllog.kafka.hbase.customer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

    public static Properties props;

    static {
        try {
            //外部加载属性文件props
            InputStream is = ClassLoader.getSystemResourceAsStream("kafka.properties");
            props = new Properties();
            props.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取属性
     */
    public static String getPorp(String key)
    {
        return props.getProperty(key);
    }

    public static String getString(String key){
        return props.getProperty(key) ;
    }

    public static int getInt(String key){
        return Integer.parseInt(props.getProperty(key)) ;
    }

}
