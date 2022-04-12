package io.terminus.debugger.common.msg;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

/**
 * @author stan
 * @date 2022/3/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvokerTunnelMessage extends TunnelMessage {

    private String beanName;

    private String methodName;


    private Collection<String> parameterTypes;

    private Collection<Byte[]> parameters;


}
