package cn.llyong.rpc.client.proxy;

import cn.llyong.rpc.client.RpcClient;
import cn.llyong.rpc.client.annotation.RpcCall;
import cn.llyong.rpc.common.bo.RpcRequest;
import cn.llyong.rpc.common.bo.RpcResponse;
import cn.llyong.rpc.common.config.Constant;
import cn.llyong.rpc.common.exception.RpcException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:  动态代理类
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 12:29 下午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcInvoker implements InvocationHandler {

    private Class<?> interfaceClass;

    @Autowired
    private RpcClient rpcClient;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> clazz = proxy.getClass().getInterfaces()[0];
        RpcCall rpcCall = clazz.getDeclaredAnnotation(RpcCall.class);
        String appName = rpcCall.name();

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString().replace("", ""));
        request.setClassName(clazz.getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        String[] paramTypes = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass().getName();
        }
        RpcResponse response = rpcClient.execute(request,appName);
        if (Constant.ERROR_CODE_200 != response.getCode()) {
            throw new RpcException("RPC调用执行异常，" + response.getMsg());
        }
        String data = response.getData();
        if (data == null) {
            return null;
        }
//        Class<?> returnType = method.getReturnType();
//        if (returnType == List.class) {
//            Type type = method.getGenericReturnType();
//            Type actualTypeArgument = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
//            JSON.parseObject(data, new TypeReference<List<Map<String, Object>>>() {
//            })
//
////            JSON.parseArray(data, type);
//        }
        return JSON.parse(data);
    }

    /**
     *
     * @param clazz
     * @param b true cglib代理/ false jdk代理
     * @param <T>
     * @return
     */
    public <T> T newProxyInstance(Class<T> clazz) {
        this.interfaceClass = clazz;
        log.info("execute jdk dynamic proxy : {}", clazz.getSimpleName());
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz}, this);
    }

}


//@Component
//public class RpcInvoker implements InvocationHandler, MethodInterceptor {
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        return null;
//    }
//
//    @Override
//    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
//        return null;
//    }
//}
