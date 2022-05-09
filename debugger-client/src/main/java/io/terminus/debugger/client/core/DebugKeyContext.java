package io.terminus.debugger.client.core;

import java.util.Optional;

/**
 * @author stan
 * @date 2022/4/11
 */
public class DebugKeyContext {

    // debugKey 的标识， 默认为 trantor-debug-key， 可通过环境变量 TERMINUS_CUSTOM_DEBUG_KEY 进行调整
    public static final String DEBUG_KEY = Optional
            .ofNullable(System.getenv("TERMINUS_CUSTOM_DEBUG_KEY"))
            .orElse("trantor-debug-key");

    private static final ThreadLocal<String> LOCAL = new ThreadLocal<>();

    public static String get() {
        return LOCAL.get();
    }

    public static void set(String str) {
        LOCAL.set(str);
    }

    public static void remove() {
        LOCAL.remove();
    }
}
