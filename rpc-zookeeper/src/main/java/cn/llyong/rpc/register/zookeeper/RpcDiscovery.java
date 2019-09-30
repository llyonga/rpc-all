package cn.llyong.rpc.register.zookeeper;

import cn.llyong.rpc.common.config.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created with IntelliJ IDEA.
 *
 * @description: 服务的发现
 * @author: lvyong
 * @date: 2019-09-29
 * @time: 9:39 上午
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcDiscovery {

    /**
     * 注册中心地址
     */
    @Value("${rpc.register.address}")
    private String registerAddress;
    /**
     * 会话超时时间
     */
    @Value("${rpc.connection.sessionTimeout}")
    private int sessionTimeout;

    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    /**
     * 可用服务列表
     */
    Map<String, List<String>> serverMap = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        try {
            connectionZk();
            countDownLatch.await();
            watchNode();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zk.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * create connection
     * @return
     */
    private void connectionZk() throws Exception{
        zk = new ZooKeeper(registerAddress, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                watchNode();
                if (event.getState().equals(Event.KeeperState.SyncConnected)){
                    countDownLatch.countDown();
                }
            }
        });
    }

    /**
     * 从注册中心获取可用服务，监听节点变化，更新服务列表
     */
    private void watchNode() {
        try {
            /**
             *  服务注册的根节点
             */
            String rootPath = Constant.ROOT_PATH;
            Stat stat = zk.exists(rootPath, false);
            if (stat != null) {
                List<String> children = zk.getChildren(rootPath, true);
                for (String child : children) {
//                byte[] data = zk.getData(rootPath + "/" + child, null, null);
//                System.out.println(new String(data));
                    serverMap.clear();
                    serverMap.put(child, zk.getChildren(rootPath + "/" + child, true));
                }
                log.info("刷新了可用服务列表：【{}】", serverMap);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("可用服务列表刷新失败！", e);
        }
    }

    /**
     * 根据服务Id获取可用节点
     * @param serverId
     * @return
     */
    public List<String> getAvailable(String serverId) {
        if (StringUtils.isEmpty(serverId)) {
            return null;
        }
        if (!serverMap.containsKey(serverId)) {
            watchNode();
        }
        return serverMap.get(serverId);
    }

}
