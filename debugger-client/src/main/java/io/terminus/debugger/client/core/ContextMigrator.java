package io.terminus.debugger.client.core;


/**
 * 上下文迁移器
 *
 * @param <T>
 */
public interface ContextMigrator<T> {
    /**
     * 在线程创建之前将上下文拿出来
     */
    T capture();

    /**
     * 在线程开始跑之前，将上下文放到目标进程中
     */
    void replay(T data);

    /**
     * 上下文清理器
     */
    void remove();

    /**
     * 单纯作为泛型丢失之后的类型转换使用
     */
    @SuppressWarnings("unchecked")
    default void adaptReplay(Object o) {
        if (o != null) {
            replay((T) o);
        }
    }
}
