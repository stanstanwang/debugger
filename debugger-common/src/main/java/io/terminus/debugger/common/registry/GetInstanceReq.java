package io.terminus.debugger.common.registry;

/**
 * @author stan
 * @date 2022/4/7
 */
public class GetInstanceReq extends DebuggerInstance {

    public GetInstanceReq() {
    }

    public GetInstanceReq(String debugKey, String instanceId) {
        super(debugKey, instanceId);
    }

    @Override
    public String toString() {
        return "GetInstanceReq{" +
                "debugKey='" + debugKey + '\'' +
                ", instanceId='" + instanceId + '\'' +
                '}';
    }
}
