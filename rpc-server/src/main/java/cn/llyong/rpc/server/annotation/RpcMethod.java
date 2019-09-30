package cn.llyong.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @description: rpc服务方法
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 10:06 上午
 * @version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RpcMethod {

}