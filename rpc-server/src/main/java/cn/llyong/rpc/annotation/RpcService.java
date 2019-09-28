package cn.llyong.rpc.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: llyong
 * @date: 2019/9/28
 * @time: 22:55
 * @version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
}
