package cn.llyong.rpc.client.consumer;

import cn.llyong.rpc.client.annotation.RpcCall;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 12:23 下午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcConsumer implements ApplicationContextAware, InitializingBean {

    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> beans = context.getBeansWithAnnotation(RpcCall.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
