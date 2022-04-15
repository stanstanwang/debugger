package io.terminus.debugger.client.invoker;

import io.terminus.debugger.client.core.DebugRepeater;
import io.terminus.debugger.common.msg.InvokerTunnelMessage;
import io.terminus.debugger.common.msg.InvokerTunnelResponse;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

/**
 * @author stan
 * @date 2022/3/23
 */
public class InvokerDebugRepeater implements DebugRepeater<InvokerTunnelMessage, InvokerTunnelResponse>, ApplicationContextAware {

    private ApplicationContext ac;


    public Mono<InvokerTunnelResponse> repeat(InvokerTunnelMessage message) {
        // 找到对应的 service 和 对应的方法

        String beanName = message.getBeanName();
        String methodName = message.getMethodName();
        Object bean = ac.getBean(beanName);

        Method[] declaredMethods = bean.getClass().getDeclaredMethods();

        // 直接反射执行
        // Class.class.getdeme
        /*Method method = null;
        try {
            return method.invoke(bean, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }*/
        return null;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
