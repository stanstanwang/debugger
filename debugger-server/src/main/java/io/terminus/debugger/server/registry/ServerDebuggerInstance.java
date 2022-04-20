package io.terminus.debugger.server.registry;

import io.terminus.debugger.common.registry.DebuggerInstance;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.rsocket.RSocketRequester;

/**
 * 服务端的注册实例，会加上一些额外信息
 *
 * @author stan
 * @date 2022/4/20
 */
@Getter
@Setter
public class ServerDebuggerInstance extends DebuggerInstance {

    /**
     * 隧道服务端到隧道客户端的连接， 用于从线上给本地的发送请求。
     */
    private RSocketRequester requester;


    public ServerDebuggerInstance() {
    }

    public ServerDebuggerInstance(String debugKey, String instanceId, RSocketRequester requester) {
        super(debugKey, instanceId);
        this.requester = requester;
    }

    // TODO test stan 2022/4/7 toString 得响应对应到本地的连接
    @Override
    public String toString() {
        return "DebuggerInstance{" +
                "debugKey='" + debugKey + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", connection=" + requester +
                '}';
    }
}
