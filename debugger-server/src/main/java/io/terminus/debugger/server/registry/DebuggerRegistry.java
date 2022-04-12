package io.terminus.debugger.server.registry;

import io.terminus.debugger.common.registry.DebuggerInstance;
import io.terminus.debugger.common.registry.GetInstanceRequest;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册中心
 *
 * @author stan
 * @date 2022/4/7
 */
public class DebuggerRegistry {


    /**
     * 所有本地 debugger 注册到当前的数据，用内存方式存取
     */
    private Map<String, DebuggerInstance> registryMap = new ConcurrentHashMap<>();

    /**
     * 将本地debugger实例注册进来
     */
    public void register(DebuggerInstance instance) {
        registryMap.put(uniqueId(instance), instance);
    }

    /**
     * 取消本地debugger实例的注册
     */
    public void unregister(DebuggerInstance instance) {
        registryMap.remove(uniqueId(instance));
    }

    /**
     * 查找debugger实例
     */
    public DebuggerInstance get(GetInstanceRequest request) {
        return registryMap.get(uniqueId(request));
    }

    /**
     * 查找当前所有debugger实例
     */
    public Collection<DebuggerInstance> list() {
        return registryMap.values();
    }

    static String uniqueId(DebuggerInstance instance) {
        return uniqueId(instance.getDebugKey(), instance.getInstanceId());
    }

    static String uniqueId(GetInstanceRequest request) {
        return uniqueId(request.getDebugKey(), request.getInstanceId());
    }

    static String uniqueId(String debugKey, String instanceId) {
        return String.format("%s:%s", debugKey, instanceId);
    }


}
