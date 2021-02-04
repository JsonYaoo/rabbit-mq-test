package com.jsonyao.cs.common.convert;

import com.google.common.base.Preconditions;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * RabbitMQ自定义消息转换器: 使用装饰者模式, 为通用消息发送时装饰过期时间等信息
 */
public class RabbitMessageConverter implements MessageConverter {

    /**
     * 过期时间: 可以用来实现延迟队列, 思路: 生产者设置消息过期时间TTL, 过期后消息投递到死信队列中, 然后消费者消费死信队列相当于消费延迟队列
     */
    public static final String DEFAULT_EXPIRE = String.valueOf(24 * 60 * 60 * 1000);

    private GenericMessageConverter delegate;

    public RabbitMessageConverter(GenericMessageConverter delegate) {
        Preconditions.checkNotNull(delegate);
        this.delegate = delegate;
    }

    /**
     * 自定义序列化发送消息
     * @param object
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
//        messageProperties.setExpiration(DEFAULT_EXPIRE);

        // 使用延迟插件实现延迟队列
        com.jsonyao.cs.api.Message message = (com.jsonyao.cs.api.Message) object;
        messageProperties.setDelay(message.getDelayMills());
        return delegate.toMessage(object, messageProperties);
    }

    /**
     * 自定义反序列化接收消息
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        return delegate.fromMessage(message);
    }
}
