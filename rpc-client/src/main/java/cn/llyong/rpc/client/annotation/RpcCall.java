package cn.llyong.rpc.client.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @description: 标识rpc调用的服务
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 12:14 下午
 * @version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RpcCall {

    /**
     * 调用的rpc服务
     * @return
     */
    String name() default "";
}
