package io.terminus.debugger.client.core;

import io.terminus.debugger.client.util.MacAddressUtil;
import io.terminus.debugger.common.registry.DebuggerInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author stan
 * @date 2022/4/7
 */
@Component
@Slf4j
public class DebuggerInstanceProvider implements ApplicationContextAware {

    private final String debugKey;

    private String instanceId;

    private DebuggerInstance instance;


    public DebuggerInstanceProvider(LocalDebugProperties debugProperties) {
        String debugKey = debugProperties.getDebugKey();
        if (!StringUtils.hasLength(debugKey)) {
            debugKey = MacAddressUtil.getLocalMacAddress();
        }

        if (!StringUtils.hasLength(debugKey)) {
            throw new IllegalArgumentException("debugKey not exists, try to config by `LocalDebugProperties.debugKey`");
        }
        log.info("**** local debug key is {}", debugKey);
        this.debugKey = debugKey;
    }


    /**
     * 获取 debugKey
     */
    public String getDebugKey() {
        return debugKey;
    }

    /**
     * 获取当前的实例id
     */
    public String getInstanceId() {
        return instanceId;
    }


    /**
     * 获取实例对象
     */
    public DebuggerInstance get() {
        if (this.instance == null) {
            this.instance = new DebuggerInstance(getDebugKey(), getInstanceId());
        }
        return instance;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.instanceId = applicationContext.getId();
    }
}
