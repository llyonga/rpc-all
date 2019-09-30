package cn.llyong.rpc.client.proxy;

import cn.llyong.rpc.client.annotation.RpcCall;
import cn.llyong.rpc.client.annotation.RpcScan;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @description: 扫描包，注册到spring容器
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 4:48 下午
 * @version: 1.0
 */
@Component
public class RpcRegistryBean implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

    private ApplicationContext ctx;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        Map<String, Object> beans = ctx.getBeansWithAnnotation(RpcScan.class);
        if (beans.isEmpty()) {
            throw new BeanCreationException("没有找到RpcScan配置！");
        }
        int i = 0;
        Collection<Object> values = beans.values();
        Class<?> beanClass;
        RpcScan rpcScan = null;
        for (Object bean : values) {
            beanClass = bean.getClass();
            rpcScan = beanClass.getDeclaredAnnotation(RpcScan.class);
            i ++;
        }
        if (i > 1) {
            throw new BeanCreationException("找到多个RpcScan配置！");
        }
        if (rpcScan.basePackage() == null) {
            throw new BeanCreationException("没有找到RpcScan配置！");
        }
        String basePackage = rpcScan.basePackage();
        // 需要被代理的接口
        Reflections reflections = new Reflections(basePackage);
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(RpcCall.class);
        for (Class<?> cls : classSet) {
//            RpcCall rpcCall = cls.getDeclaredAnnotation(RpcCall.class);
//            String name = rpcCall.name();
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(cls);
            GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
            definition.getPropertyValues().add("interfaceClass", definition.getBeanClassName());
            definition.setBeanClass(RpcProxyFactory.class);
            definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

            char [] chars = cls.getSimpleName().toCharArray();
            chars[0] += 32;
            String beanName = String.valueOf(chars);
            // 注册bean名,一般为类名首字母小写
            beanDefinitionRegistry.registerBeanDefinition(beanName, definition);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.ctx = context;
    }

}
