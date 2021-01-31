package com.jsonyao.cs.common.serializer;

/**
 * 自定义序列化器
 */
public interface Serializer {

    /**
     * 序列化Object对象为字节数组
     * @param data
     * @return
     */
    byte[] serializeRaw(Object data);

    /**
     * 序列化Object对象为字符串
     * @param data
     * @return
     */
    String serialize(Object data);

    /**
     * 序列化字符串Content为指定类型对象
     * @param content
     * @return
     */
    <T> T deserialize(String content);

    /**
     * 序列化字节数组Content为指定类型对象
     * @param content
     * @return
     */
    <T> T deserialize(byte[] content);
}
