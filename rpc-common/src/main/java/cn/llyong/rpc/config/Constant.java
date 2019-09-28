package cn.llyong.rpc.config;

/**
 * @description: 常量接口
 * @author: llyong
 * @date: 2019/9/28
 * @time: 22:06
 * @version: 1.0
 */
public interface Constant {
    /**
     * zookeeper session超时事件 ms
     */
    int SESSION_TIME_OUT = 20000;
    /**
     * zookeeper中rpc根节点路径
     */
    String ROOT_PATH = "/rpcRoot";
    /**
     * zookeeper地址
     */
    String ADDRESS = "127.0.0.1:2181";
}
