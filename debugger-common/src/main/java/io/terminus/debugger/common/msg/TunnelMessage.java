package io.terminus.debugger.common.msg;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 透传的消息
 *
 * @author stan
 * @date 2022/3/21
 */
// 多态的时候定义序列化和反序列化怎么保存类型信息
@JsonTypeInfo(
        // 值是啥
        use = JsonTypeInfo.Id.CLASS,
        // 放在哪里
        include = JsonTypeInfo.As.PROPERTY,
        // 标识是什么
        property = "@@class")
public abstract class TunnelMessage {

    /**
     * 获取该隧道消息对应需要路由到哪里
     */
    public abstract String getRoute();


}
