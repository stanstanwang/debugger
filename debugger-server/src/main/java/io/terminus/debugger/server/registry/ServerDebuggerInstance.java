package io.terminus.debugger.server.registry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.terminus.debugger.common.registry.DebuggerInstance;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.util.Date;

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
    @JsonIgnore
    private RSocketRequester requester;

    /**
     * 注册什么时候
     */
    private Date registerAt;


    public ServerDebuggerInstance() {
    }

    public ServerDebuggerInstance(String debugKey, String instanceId, RSocketRequester requester) {
        super(debugKey, instanceId);
        this.requester = requester;
        this.registerAt = new Date();
    }

    @Override
    public String toString() {
        return "ServerDebuggerInstance{" +
                "debugKey='" + debugKey + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", registerAt=" + registerAt +
                '}';
    }
}
