package cn.llyong.rpc.register.zookeeper;

import cn.llyong.rpc.common.config.Constant;
import cn.llyong.rpc.common.exception.RpcException;
import cn.llyong.rpc.common.helper.IpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @description: 服务的注册
 * @author: llyong
 * @date: 2019/9/28
 * @time: 23:13
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcRegistry implements InitializingBean {

    /**
     *  服务注册的根节点
     */
    private String rootPath = Constant.ROOT_PATH;
    /**
     * 注册服务节点
     */
    private String serverPath = "";
    /**
     * 应用实例节点
     */
    private String appPath = "";
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
    /**
     * 服务端口号
     */
    @Value("${server.port}")
    private int port;
    /**
     * 服务名称
     */
    @Value("${spring.application.name}")
    private String appName;
    /**
     * 服务主机地址
     */
    private String host = IpHelper.getRealIp();

    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            connectionZk();
            countDownLatch.await();
        } catch (Exception e) {
            throw new ApplicationContextException("", e);
        }
        createRootNode();
        createServiceNode();
        createAppNode();
    }

    /**
     * create connection
     * @return
     */
    private void connectionZk() throws IOException {
        zk = new ZooKeeper(registerAddress, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState().equals(Event.KeeperState.SyncConnected)){
                    countDownLatch.countDown();
                }
            }
        });
    }

    /**
     * 创建根目录 rootPath
     */
    private void createRootNode()  {
        try {
            Stat stat = zk.exists(rootPath, false);
            if (stat == null) {
                zk.create(rootPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("根节点: 【{}】创建成功!", rootPath);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("根节点: 【{}】创建失败!", rootPath, e);
        }
    }

    /**
     * 在rootPath根目录下，创建注册服务子节点
     */
    private void createServiceNode() {
        try {
            if(StringUtils.isEmpty(appName)) {
                throw new RpcException("the spring.application.name is null");
            }
            serverPath = rootPath + "/" + appName;
            Stat stat = zk.exists(serverPath, false);
            if (stat == null) {
                zk.create(serverPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                log.info("服务节点: 【{}】创建成功!", serverPath);
            }
        } catch (Exception e) {
            log.info("服务节点: 【{}】创建失败!", serverPath, e);
        }
    }

    /**
     * 创建应用实例节点
     */
    private void createAppNode() {
        try {
            appPath = serverPath + "/" + host + "|" + port;
            Stat stat = zk.exists(appPath, false);
            if (stat == null) {
                //最终节点创建为临时节点
                zk.create(appPath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                log.info("应用实例节点: 【{}】创建成功!", appPath);
            }
        } catch (Exception e) {
            log.info("应用实例节点: 【{}】创建成功!", appPath, e);
        }
    }
}
