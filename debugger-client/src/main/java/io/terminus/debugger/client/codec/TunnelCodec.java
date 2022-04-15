package io.terminus.debugger.client.codec;


/**
 * 序列化和反序列化组件
 *
 * @author stan
 * @date 2022/3/23
 */
public interface TunnelCodec {

    byte[] encode(Object data);

    <T> T decode(byte[] data, Class<T> c);

    // <T> T decode(byte[] data, TypeReference<T> typeReference);

}
