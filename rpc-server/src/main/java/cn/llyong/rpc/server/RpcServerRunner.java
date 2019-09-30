package cn.llyong.rpc.server;

import cn.llyong.rpc.common.exception.RpcException;
import cn.llyong.rpc.common.handler.ServerMarshallingHandler;
import cn.llyong.rpc.common.helper.IpHelper;
import cn.llyong.rpc.server.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 10:29 上午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcServerRunner implements InitializingBean, DisposableBean {

    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    private ServerBootstrap serverBootstrap;

    @Value("${server.port}")
    private int port;

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() throws Exception {
        serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 60));
//                        pipeline.addLast(new ServerMarshallingHandler());
                        pipeline.addLast(new RpcRequestHandler());
                    }
                });
        try {
            ChannelFuture future = serverBootstrap.bind(port).sync();
            log.info("rpc server 启动监听端口： 【{}】", port);

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RpcException("rpc server init fail.", e);
        }
    }

    @Override
    public void destroy() throws Exception {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}