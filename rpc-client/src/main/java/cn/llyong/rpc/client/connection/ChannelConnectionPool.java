package cn.llyong.rpc.client.connection;

import cn.llyong.rpc.client.RpcClient;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019/9/29
 * @time: 10:48 下午
 * @version: 1.0
 */
@Component
@Slf4j
public class ChannelConnectionPool {

    @Autowired
    private RpcClient rpcClient;

    private Map<SocketAddress, Channel> channelNodes = new ConcurrentHashMap<>();

    public  Channel getChannel(SocketAddress address) {
        Channel channel = channelNodes.get(address);
        if (channel == null) {
            log.info("当前没有空闲连接，开始重建连接通道！");
            channel = reconnect(address);
        }
        return channel;
    }

    private synchronized Channel reconnect(SocketAddress address) {
        Channel channel = null;
        try {
            channel = rpcClient.connect(address);
            channelNodes.put(address, channel);
            log.info("添加channel到连接管理器！");
        } catch (InterruptedException e) {
            log.error("尝试重新连接服务失败！", e);
        }
        return channel;
    }
}
