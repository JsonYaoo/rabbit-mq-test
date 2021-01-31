package com.jsonyao.cs.common.serializer.impl;

import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.common.serializer.Serializer;
import com.jsonyao.cs.common.serializer.SerializerFactory;

/**
 * 自定义序列化器工厂实现类
 */
public class JacksonSerializerFactory implements SerializerFactory {

    /**
     * SerializerFactory饿汉式单例
     */
    public static final SerializerFactory INSTANCE = new JacksonSerializerFactory();

    @Override
    public Serializer create() {
        return JacksonSerializer.createParametricType(Message.class);
    }
}
