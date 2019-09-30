package cn.llyong.rpc.common.handler;

import cn.llyong.rpc.common.bo.RpcRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @description: 
 * @author: lvyong
 * @date: 2019/9/26 
 * @time: 11:06 上午
 * @version: 1.0
 */
public class ServerMarshallingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest request = (RpcRequest) msg;
        System.out.println("服务端收到消息：" + request);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}