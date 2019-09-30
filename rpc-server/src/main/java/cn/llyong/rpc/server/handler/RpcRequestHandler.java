package cn.llyong.rpc.server.handler;

import cn.llyong.rpc.common.bo.RpcRequest;
import cn.llyong.rpc.common.bo.RpcResponse;
import cn.llyong.rpc.common.config.Constant;
import cn.llyong.rpc.register.zookeeper.RpcDiscovery;
import cn.llyong.rpc.server.provider.RpcProvider;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 10:56 上午
 * @version: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RpcProvider rpcProvider;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        log.info("服务端收到请求对象: {}", request);
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        response.setCode(Constant.ERROR_CODE_200);
        response.setMsg("success");
        try {
            String className = request.getClassName();
            Object target = context.getBean(className);
            Method method = target.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            Map<String, List<String>> apiMap = rpcProvider.getApiMap();
            List<String> list = apiMap.get(className);
            if (!list.contains(method)) {
                response.setCode(Constant.ERROR_CODE_404);
                response.setMsg("调用的方法不存在或者没有权限");
            }
            Object result = method.invoke(target, request.getParameters());
            String jsonString = JSON.toJSONString(result);
            response.setData(jsonString);
        }catch (Exception e){
            log.error("Rpc execute Error!", e);
            response.setCode(Constant.ERROR_CODE_500);
            response.setMsg(e.getMessage());
        }
        byte[] bytes = JSON.toJSONString(response).getBytes("UTF-8");
        ctx.writeAndFlush(Unpooled.copiedBuffer(bytes));
    }

}
