package cn.llyong.rpc.client;

import cn.llyong.rpc.client.connection.ChannelConnectionPool;
import cn.llyong.rpc.client.handler.RpcResponseHandler;
import cn.llyong.rpc.common.bo.RpcRequest;
import cn.llyong.rpc.common.bo.RpcResponse;
import cn.llyong.rpc.common.exception.RpcException;
import cn.llyong.rpc.register.zookeeper.RpcDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 10:52 上午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcClient {

    @Autowired
    private RpcDiscovery rpcDiscovery;

    @Autowired
    private ChannelConnectionPool channelConnectionPool;

    @Autowired
    private RpcResponseHandler rpcResponseHandler;

    private EventLoopGroup group;
    private Bootstrap bootstrap;

    public RpcClient() {
        group = new NioEventLoopGroup();

        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 30));
//                        pipeline.addLast(new JSONEncoder());
//                        pipeline.addLast(new JSONDecoder());
//                        pipeline.addLast(new ClientMarshallingHandler());
                        pipeline.addLast(new RpcResponseHandler());
                    }
                });
    }

    public Channel connect(SocketAddress address) throws InterruptedException {
        ChannelFuture future = bootstrap.connect(address);
        Channel channel = future.sync().channel();
        return channel;
    }

    @PostConstruct
    public RpcResponse execute(RpcRequest request, String serverId) throws RpcException, InterruptedException {
        List<String> list = rpcDiscovery.getAvailable(serverId);
        if (CollectionUtils.isEmpty(list)) {
            throw new RpcException("没有发现【"+ serverId +"】可用的服务");
        }
        String[] split = list.get(0).split("|");
        InetSocketAddress address = new InetSocketAddress(split[0], Integer.parseInt(split[1]));
        Channel channel = channelConnectionPool.getChannel(address);
        SynchronousQueue<Object> queue = rpcResponseHandler.send(request, channel);
        RpcResponse response = (RpcResponse) (queue.take());
        return response;
    }

    @PreDestroy
    public void destroy() {
        log.info("RPC客户端退出,释放资源!");
        group.shutdownGracefully();
    }
}
