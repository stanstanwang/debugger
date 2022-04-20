package io.terminus.debugger.common.registry;

import lombok.Getter;
import lombok.Setter;

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
    protected String debugKey;

    /**
     * 实例id， 用于区分同个用户不同 debugger 服务
     */
    protected String instanceId;


    public DebuggerInstance() {
    }

    public DebuggerInstance(String debugKey, String instanceId) {
        this.debugKey = debugKey;
        this.instanceId = instanceId;
    }


}
