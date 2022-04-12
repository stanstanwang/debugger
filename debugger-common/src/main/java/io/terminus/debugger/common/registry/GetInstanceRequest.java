package io.terminus.debugger.common.registry;

import lombok.Data;

/**
 * @author stan
 * @date 2022/4/7
 */
@Data
public class GetInstanceRequest {

    /**
     * debugKey, 用于区分不同用户的debugKey
     */
    private String debugKey;

    /**
     * 实例id， 用于区分同个用户不同 debugger 服务
     */
    private String instanceId;


    public GetInstanceRequest() {
    }


    public GetInstanceRequest(String debugKey, String instanceId) {
        this.debugKey = debugKey;
        this.instanceId = instanceId;
    }
}
