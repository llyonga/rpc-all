package cn.llyong.rpc.client.handler;

import cn.llyong.rpc.common.bo.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 12:08 下午
 * @version: 1.0
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Resource
    private RpcRequestPool requestPool;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        log.info("rpc调用返回数据 : {}", response);
        requestPool.notifyRequest(response.getRequestId(), response);
    }
}
