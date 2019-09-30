package cn.llyong.rpc.server.provider;

import cn.llyong.rpc.server.annotation.RpcMethod;
import cn.llyong.rpc.server.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 9:36 上午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcProvider implements ApplicationContextAware {

    private Map<String, List<String>> apiMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Object> rpcServices = context.getBeansWithAnnotation(RpcService.class);
        if (!CollectionUtils.isEmpty(rpcServices)){
            rpcServices.values().forEach(bean -> {
                Class<?> clazz = bean.getClass();
                Method[] declaredMethods = clazz.getDeclaredMethods();
                List<String> methodList = new ArrayList<>();
                for (Method method : declaredMethods) {
                    RpcMethod rpcMethod = method.getDeclaredAnnotation(RpcMethod.class);
                    if (rpcMethod != null) {
                        methodList.add(method.getName());
                    }
                }
                apiMap.put(clazz.getName(), methodList);
                log.info("已加载所有服务类: {}", apiMap);
//                Class<?>[] interfaces = clazz.getInterfaces();
//                for (Class<?> inter : interfaces){
//                    String interfaceName = inter.getName();
//                    log.info("已加载服务类: {}", interfaceName);
//                }
            });
        }
    }

    public Map<String, List<String>> getApiMap() {
        return apiMap;
    }
}
