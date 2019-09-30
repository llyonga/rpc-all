package cn.llyong.rpc.client.proxy;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:  代理工厂类
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 12:31 下午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcProxyFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    public Class<T> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public T getObject() throws Exception {
        return (T) new RpcInvoker().newProxyInstance(interfaceClass);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    /**
     * 单例模式
      */
    @Override
    public boolean isSingleton() {
        return true;
    }

}