package cn.llyong.rpc.bo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @description: RPC请求对象
 * @author: llyong
 * @date: 2019/9/28
 * @time: 21:41
 * @version: 1.0
 */
@Data
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public RpcRequest(){}

    @Builder
    public RpcRequest(String requestId, String className, String methodName,
                      Class<?>[] parameterTypes, Object[] parameters) {
        this.requestId = requestId;
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"requestId\":\"")
                .append(requestId).append('\"');
        sb.append(",\"className\":\"")
                .append(className).append('\"');
        sb.append(",\"methodName\":\"")
                .append(methodName).append('\"');
        sb.append(",\"parameterTypes\":")
                .append(Arrays.toString(parameterTypes));
        sb.append(",\"parameters\":")
                .append(Arrays.toString(parameters));
        sb.append('}');
        return sb.toString();
    }
}
