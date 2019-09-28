package cn.llyong.rpc.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-27
 * @time: 12:29 下午
 * @version: 1.0
 */
public class IService implements Serializable {

    private final long serialVersionUID = 1L;

    private String serviceId;
    private List<INode> nodes;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<INode> getNodes() {
        return nodes;
    }

    public void setNodes(List<INode> nodes) {
        this.nodes = nodes;
    }
}
