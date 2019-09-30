package cn.llyong.rpc.common.bo;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @description:
 * @author: lvyong
 * @date: 2019-09-28
 * @time: 2:08 上午
 * @version: 1.0
 */
public class INode implements Serializable {

    private final long serialVersionUID = 1L;

    private String nodeId;
    private String metaData;

    public INode() {
    }

    public INode(String nodeId, String metaData) {
        this.nodeId = nodeId;
        this.metaData = metaData;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
