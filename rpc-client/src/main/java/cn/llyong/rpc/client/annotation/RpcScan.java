package cn.llyong.rpc.client.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:  配置rpc接口扫描的包
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 5:11 下午
 * @version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface RpcScan {

    String basePackage() default "";
}
