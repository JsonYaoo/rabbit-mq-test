package com.jsonyao.cs.producer.broker;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.api.MessageType;
import com.jsonyao.cs.api.exception.MessageRunTimeException;
import com.jsonyao.cs.common.convert.GenericMessageConverter;
import com.jsonyao.cs.common.convert.RabbitMessageConverter;
import com.jsonyao.cs.common.serializer.Serializer;
import com.jsonyao.cs.common.serializer.SerializerFactory;
import com.jsonyao.cs.common.serializer.impl.JacksonSerializerFactory;
import com.jsonyao.cs.producer.service.impl.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * RabbitTemplate池化封装: 缓存Topic RabbitTemplate, 以提高性能和定制化Template
 */
@Slf4j
@Component
public class RabbitTemplateContainer implements RabbitTemplate.ConfirmCallback{

    /**
     * RabbitTemplateMap: Topic RabbitTemplate缓存
     */
    private Map<String, RabbitTemplate> rabbitTemplateMap = Maps.newConcurrentMap();

    /**
     * #号分隔器
     */
    private Splitter splitter = Splitter.on("#");

    /**
     * SerializerFactory饿汉式单例
     */
    private SerializerFactory serializerFactory = JacksonSerializerFactory.INSTANCE;

    /**
     * RabbitTemplate会话连接工厂
     */
    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MessageStoreService messageStoreService;

    /**
     * 获取RabbitTemplate实例
     * @param message
     * @return
     * @throws MessageRunTimeException
     */
    public RabbitTemplate getRabbitTemplate(Message message) throws MessageRunTimeException {
        Preconditions.checkNotNull(message);

        String topic = message.getTopic();
        Preconditions.checkNotNull(topic);

        // 查询Topic RabbitTemplate缓存
        RabbitTemplate rabbitTemplate = rabbitTemplateMap.get(topic);
        if(rabbitTemplate != null) return rabbitTemplate;
        log.info("#RabbitTemplateContainer.getTemplate# topic: {} is not exists, create one", topic);

        // 设置RabbitTemplate基础信息
        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setExchange(topic);
        rabbitTemplate.setRoutingKey(message.getRoutingKey());
        rabbitTemplate.setRetryTemplate(new RetryTemplate());

        // 添加序列化反序列化和converter对象: 自定义序列化和反序列化消息 -> 自定义Message对象
        Serializer serializer = serializerFactory.create();
        GenericMessageConverter genericMessageConverter = new GenericMessageConverter(serializer);
        RabbitMessageConverter rabbitMessageConverter = new RabbitMessageConverter(genericMessageConverter);
        rabbitTemplate.setMessageConverter(rabbitMessageConverter);

        // 为Confirm和RELIANT消息设置消息确认回调方法
        String messageType = message.getMessageType();
        if(!MessageType.RAPID.equals(messageType)){
            rabbitTemplate.setConfirmCallback(this);
        }

        // 设置Topic RabbitTemplate缓存
        rabbitTemplateMap.putIfAbsent(topic, rabbitTemplate);
        return rabbitTemplate;
    }

    /**
     * 消息确认: Confirm消息触发确认逻辑(但不落库), Reliant消息(已落库)则用于保证可靠性投递
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        List<String> strings = splitter.splitToList(correlationData.getId());
        // 获取参数
        String messageId = strings.get(0);
        long sendTime = Long.parseLong(strings.get(1));
        String messageType = strings.get(2);
        if(ack) {
            // 当Broker返回ACK成功时, Reliant消息需要更新已落库消息状态为SEND_OK, 否则是通过定时器保证投递的可靠性
            if(MessageType.RELIANT.equals(messageType)){
                messageStoreService.succuess(messageId);
            }

            // 触发ACK Confirm业务
            log.info("send message is OK, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        } else {
            // 触发NACK Confirm业务
            log.error("send message is Fail, confirm messageId: {}, sendTime: {}", messageId, sendTime);
        }
    }
}
