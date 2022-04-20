package io.terminus.debugger.client.core;

/**
 * @author stan
 * @date 2022/4/11
 */
public class DebugKeyContext {

    // TODO stan 2022/4/19 需要可配置
    public static String DEBUG_KEY = "trantor-debug-key";

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
