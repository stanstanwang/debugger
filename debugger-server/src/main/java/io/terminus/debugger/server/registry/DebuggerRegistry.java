package io.terminus.debugger.server.registry;

import io.terminus.debugger.common.registry.DebuggerInstance;
import io.terminus.debugger.common.registry.DelInstanceReq;
import io.terminus.debugger.common.registry.GetInstanceReq;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心
 *
 * @author stan
 * @date 2022/4/7
 */
@Component
public class DebuggerRegistry {


    /**
     * 所有本地 debugger 注册到当前的数据，用内存方式存取
     */
    private final Map<String, ServerDebuggerInstance> registryMap = new ConcurrentHashMap<>();

    /**
     * 将本地debugger实例注册进来
     */
    public void register(ServerDebuggerInstance instance) {
        registryMap.put(uniqueId(instance), instance);
    }

    /**
     * 取消本地debugger实例的注册
     */
    public void unregister(DelInstanceReq instance) {
        registryMap.remove(uniqueId(instance));
    }

    /**
     * 查找debugger实例
     */
    public ServerDebuggerInstance get(GetInstanceReq request) {
        return registryMap.get(uniqueId(request));
    }

    /**
     * 查找当前所有debugger实例
     */
    public Collection<ServerDebuggerInstance> list() {
        return registryMap.values();
    }

    static String uniqueId(DebuggerInstance instance) {
        return uniqueId(instance.getDebugKey(), instance.getInstanceId());
    }

    static String uniqueId(GetInstanceReq request) {
        return uniqueId(request.getDebugKey(), request.getInstanceId());
    }

    static String uniqueId(String debugKey, String instanceId) {
        return String.format("%s:%s", debugKey, instanceId);
    }


}
