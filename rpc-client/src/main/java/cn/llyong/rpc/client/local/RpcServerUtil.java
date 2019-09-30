package cn.llyong.rpc.client.local;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 8:47 下午
 * @version: 1.0
 */
@Component
public class RpcServerUtil {

    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
}
