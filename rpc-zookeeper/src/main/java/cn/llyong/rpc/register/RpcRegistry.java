package cn.llyong.rpc.register;

import cn.llyong.rpc.config.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @author: llyong
 * @date: 2019/9/28
 * @time: 23:13
 * @version: 1.0
 */
@Component
@Slf4j
public class RpcRegistry implements ApplicationContextAware {

    /**
     *  服务注册的根节点
     */
    private final String ROOT_PATH = Constant.ROOT_PATH;
    /**
     * 注册中心地址
     */
    @Value("${rpc.register.address}")
    private String REGISTRY_ADDRESS;
    /**
     * 会话超时时间
     */
    @Value("${rpc.connection.sessionTimeout}")
    private int SESSION_TIMEOUT;
    /**
     * 服务主机地址
     */
    private String serverHost = IpHelper;
    /**
     * 服务端口号
     */
    @Value("${netty.port}")
    private int serverPort;

    private ZooKeeper zk;

    private CountDownLatch countDownLatch = new CountDownLatch(1);


    /**
     * create connection
     * @return
     */
    private void connectionZk() throws IOException {
        zk = new ZooKeeper(REGISTRY_ADDRESS, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
//                if (event.getType() == Event.EventType.NodeChildrenChanged && ROOT_PATH.equals(event.getPath())) {
//                    getAvailableServers();
//                }
//                try {
//                    System.out.println("监听执行。。。。。");
//                    System.out.println(event);
//                } catch (Exception e) {
//                    logger.error("", e);
//                }
            }
        });
    }

    /**
     * 注册服务
     * @param bService
     * @throws Exception
     */
    public void register(IService bService) throws Exception {
        if (bService != null) {
            if (zk == null) {
                connectionZk();
            }
            addRootNode();
            createNode(bService);
        }
    }

    /**
     * 创建根目录 ROOT_PATH
     */
    private void addRootNode()  {
        try {
            Stat stat = zk.exists(ROOT_PATH, false);
            if (stat == null) {
                zk.create(ROOT_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在ROOT_PATH根目录下，创建子节点
     * @param service
     */
    private void createNode(IService service) {
        try {
            String APP_PATH = ROOT_PATH + "/" + service.getServiceId();
            Stat stat = zk.exists(APP_PATH, false);
            if (stat == null) {
                //最终节点创建为临时节点
                String str = zk.create(APP_PATH,
                        service.getNodes().get(0).getMetaData().getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,
                        CreateMode.EPHEMERAL);
                logger.info("节点创建成功【{}】", str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

}
