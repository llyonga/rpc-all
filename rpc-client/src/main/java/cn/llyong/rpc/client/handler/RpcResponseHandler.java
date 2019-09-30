package cn.llyong.rpc.client.handler;

import cn.llyong.rpc.common.bo.RpcRequest;
import cn.llyong.rpc.common.bo.RpcResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

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

//    @Resource
//    private RpcRequestPool requestPool;

    private Map<String, SynchronousQueue<Object>> queueMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        log.info("rpc调用返回数据 : {}", response);
        String requestId = response.getRequestId();
        SynchronousQueue<Object> queue = queueMap.get(requestId);
        queue.put(response);
        queueMap.remove(requestId);
    }

    public SynchronousQueue<Object> send(RpcRequest request, Channel channel) {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();
        queueMap.put(request.getRequestId(), queue);
        String jsonString = JSON.toJSONString(request);
        channel.writeAndFlush(Unpooled.copiedBuffer(jsonString.getBytes(CharsetUtil.UTF_8)));
        return queue;
    }
}
