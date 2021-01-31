package com.jsonyao.cs.common.serializer;

/**
 * 自定义序列化器工厂
 */
public interface SerializerFactory {

    /**
     * 构造自定义序列化器
     * @return
     */
    Serializer create();

}
