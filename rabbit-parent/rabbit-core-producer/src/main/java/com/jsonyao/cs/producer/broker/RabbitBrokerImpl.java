package com.jsonyao.cs.producer.broker;

import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.api.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * RabbitMq消息发送实现类
 */
@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {

    }

    @Override
    public void reliantSend(Message message) {

    }

    @Override
    public void sendMessages() {

    }

    /**
     * 发送消息核心方法: 使用异步线程池进行发送消息
     * @param message
     */
    private void sendKernel(Message message){
        AsyncBaseQueue.submit((Runnable) ()-> {
            CorrelationData correlationData = new CorrelationData(String.format("%s#%s%#%s", message.getMessageId(), System.currentTimeMillis(), message.getMessageType()));
            String topic = message.getTopic();
            String routingKey = message.getRoutingKey();
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());
        });
    }
}
