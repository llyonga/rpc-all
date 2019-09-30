package cn.llyong.rpc.server.annotation;

import java.lang.annotation.*;

/**
 * @description: 标识RPC服务
 * @author: llyong
 * @date: 2019/9/28
 * @time: 22:55
 * @version: 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RpcService {
}
