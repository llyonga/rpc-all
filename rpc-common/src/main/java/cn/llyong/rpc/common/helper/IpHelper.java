package cn.llyong.rpc.common.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-27
 * @time: 10:39 上午
 * @version: 1.0
 */
public class IpHelper {

    private static final Logger logger = LoggerFactory.getLogger(IpHelper.class);

    private static String hostIp = StringUtils.EMPTY;

    static {
        String ip = null;
        Enumeration allNetInterfaces;
        try {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
                for (InterfaceAddress add : InterfaceAddress) {
                    InetAddress Ip = add.getAddress();
                    if (Ip != null && Ip instanceof Inet4Address) {
                        if (StringUtils.equals(Ip.getHostAddress(), "127.0.0.1")) {
                            continue;
                        }
                        ip = Ip.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            logger.warn("获取本机Ip失败:异常信息:" + e.getMessage());
            throw new RuntimeException(e);
        }
        hostIp = ip;
    }

    /**
     * 获取本机Ip
     * <p/>
     * 通过 获取系统所有的networkInterface网络接口 然后遍历 每个网络下的InterfaceAddress组。
     * 获得符合 <code>InetAddress instanceof Inet4Address</code> 条件的一个IpV4地址
     *
     * @return
     */
    public static String localIp() {
        return hostIp;
    }

    public static String getRealIp() {
        // 本地IP，如果没有配置外网IP则返回它
        String localip = null;
        // 外网IP
        String netip = null;
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            // 是否找到外网IP
            boolean finded = false;
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {
                        // 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {
                        // 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
            if (netip != null && !"".equals(netip)) {
                return netip;
            } else {
                return localip;
            }
        } catch (SocketException e) {
            logger.warn("获取本机Ip失败:异常信息:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
