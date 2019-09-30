package cn.llyong.rpc.common.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:
 * @author: llyong
 * @date: 2019/9/28
 * @time: 21:54
 * @version: 1.0
 */
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private int code;
    private String msg;
    private String data;


    public RpcResponse() {
    }

    @Builder
    public RpcResponse(String requestId, int code, String msg, String data) {
        this.requestId = requestId;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"requestId\":\"")
                .append(requestId).append('\"');
        sb.append(",\"code\":")
                .append(code);
        sb.append(",\"msg\":\"")
                .append(msg).append('\"');
        sb.append(",\"data\":\"")
                .append(data).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
