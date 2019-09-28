package cn.llyong.rpc.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-27
 * @time: 10:47 上午
 * @version: 1.0
 */
public class PropertyConfigeHelper {

    private static final Logger logger = LoggerFactory.getLogger(PropertyConfigeHelper.class);

    private static final String PROPERTY_CLASSPATH = "/rpc.properties";
    private static final Properties properties = new Properties();

    /**
     * 注册中心地址
     */
    private static String registryAddress;
    /**
     * 注册中心 session超时时间
     */
    private static int registrySessionTimeout;
    /**
     * 注册中心 connection超时时间
     */
    private static int registryConnectionTimeout;
    /**
     * 注册服务的地址
     */
    private static String serverAddress;
    /**
     * 注册服务的端口
     */
    private static int serverPort ;

    /**
     * 每个服务端提供者的Netty的连接数
     */
    private static int channelConnectSize;


    /**
     * 初始化
     */
    static {
        InputStream is = null;
        try {
            is = PropertyConfigeHelper.class.getResourceAsStream(PROPERTY_CLASSPATH);
            if (null == is) {
                throw new IllegalStateException("rpc.properties can not found in the classpath.");
            }
            properties.load(is);

            registryAddress = properties.getProperty("registry_address");
            registrySessionTimeout = Integer.parseInt(properties.getProperty("registry_sessionTimeout", "500"));
            registryConnectionTimeout = Integer.parseInt(properties.getProperty("registry_connectionTimeout", "500"));
            channelConnectSize = Integer.parseInt(properties.getProperty("channel_connect_size", "10"));


        } catch (Throwable t) {
            logger.warn("load ares_remoting's properties file failed.", t);
            throw new RuntimeException(t);
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getRegistryAddress() {
        return registryAddress;
    }

    public static void setRegistryAddress(String registryAddress) {
        PropertyConfigeHelper.registryAddress = registryAddress;
    }

    public static int getRegistrySessionTimeout() {
        return registrySessionTimeout;
    }

    public static void setRegistrySessionTimeout(int registrySessionTimeout) {
        PropertyConfigeHelper.registrySessionTimeout = registrySessionTimeout;
    }

    public static int getRegistryConnectionTimeout() {
        return registryConnectionTimeout;
    }

    public static void setRegistryConnectionTimeout(int registryConnectionTimeout) {
        PropertyConfigeHelper.registryConnectionTimeout = registryConnectionTimeout;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static void setServerAddress(String serverAddress) {
        PropertyConfigeHelper.serverAddress = serverAddress;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static void setServerPort(int serverPort) {
        PropertyConfigeHelper.serverPort = serverPort;
    }
}
