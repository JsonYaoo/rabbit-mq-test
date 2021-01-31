package com.jsonyao.cs.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Correlation;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * RabbitMQ: Producer
 */
@Component
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 消息Confirm机制
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println("消息ACK结果:" + ack + ", correlationData: " + correlationData.getId());
        }
    };

    /**
     * 对外发送消息
     * @param message
     * @param properties
     */
    public void send(Object message, Map<String, Object> properties){
        // 封装Message
        MessageHeaders messageHeaders = new MessageHeaders(properties);
        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);

        // 指定业务唯一ID
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        // 设置消息发送后置处理器
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {
                System.out.println("post to do: " + message);
                return null;
            }

            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message, Correlation correlation) {
                System.out.println("post to do " + message + " and correlation: " + correlation);
                return null;
            }
        };

        // 设置消息Confirm回调函数
        rabbitTemplate.setConfirmCallback(confirmCallback);

        // 发送消息
        rabbitTemplate.convertAndSend("exchange-1", "springboot.rabbit", msg, messagePostProcessor, correlationData);
    }

}
