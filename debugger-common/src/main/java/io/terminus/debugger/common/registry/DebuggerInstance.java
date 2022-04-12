package io.terminus.debugger.common.registry;

import lombok.*;

/**
 * 注册中心注册实例的定义
 *
 * @author stan
 * @date 2022/4/7
 */
@Getter
@Setter
public class DebuggerInstance {


    /**
     * debugKey, 用于区分不同用户的debugKey
     */
    private String debugKey;

    /**
     * 实例id， 用于区分同个用户不同 debugger 服务
     */
    private String instanceId;
    /**
     * 该实例对应的连接
     */
    private DebuggerConnection connection;

    public DebuggerInstance() {
    }

    public DebuggerInstance(String debugKey, String instanceId) {
        this.debugKey = debugKey;
        this.instanceId = instanceId;
    }

    public DebuggerInstance(String debugKey, String instanceId, DebuggerConnection connection) {
        this.debugKey = debugKey;
        this.instanceId = instanceId;
        this.connection = connection;
    }

    @Override
    public String toString() {
        return "DebuggerInstance{" +
                "debugKey='" + debugKey + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", connection=" + connection +
                '}';
    }
}
