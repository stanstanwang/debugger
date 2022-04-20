package io.terminus.debugger.common.registry;

/**
 * @author stan
 * @date 2022/4/7
 */
public class DelInstanceReq extends DebuggerInstance {
    public DelInstanceReq() {
    }

    public DelInstanceReq(String debugKey, String instanceId) {
        super(debugKey, instanceId);
    }
}
