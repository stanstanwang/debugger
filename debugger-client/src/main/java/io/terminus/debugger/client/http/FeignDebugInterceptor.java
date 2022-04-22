package io.terminus.debugger.client.http;

import feign.RequestTemplate;
import io.terminus.debugger.client.core.DebugInterceptor;
import io.terminus.debugger.client.core.DebugKeyContext;

import java.util.Optional;

public class FeignDebugInterceptor implements feign.RequestInterceptor, DebugInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        Optional.ofNullable(DebugKeyContext.get())
                .ifPresent(debugKey -> template.header(DebugKeyContext.DEBUG_KEY, debugKey));
    }
}
