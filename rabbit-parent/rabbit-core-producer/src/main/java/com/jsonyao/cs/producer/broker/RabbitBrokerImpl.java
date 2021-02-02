package com.jsonyao.cs.producer.broker;

import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.api.MessageType;
import com.jsonyao.cs.producer.constant.BrokerMessageConst;
import com.jsonyao.cs.producer.constant.BrokerMessageStatus;
import com.jsonyao.cs.producer.entity.BrokerMessage;
import com.jsonyao.cs.producer.service.impl.MessageStoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * RabbitMq消息发送实现类
 */
@Slf4j
@Component
public class RabbitBrokerImpl implements RabbitBroker{

    @Autowired
    private RabbitTemplateContainer rabbitTemplateContainer;

    @Autowired
    private MessageStoreService messageStoreService;

    @Override
    public void rapidSend(Message message) {
        message.setMessageType(MessageType.RAPID);
        sendKernel(message);
    }

    @Override
    public void confirmSend(Message message) {
        message.setMessageType(MessageType.CONFIRM);
        sendKernel(message);
    }

    @Override
    public void reliantSend(Message message) {
        message.setMessageType(MessageType.RELIANT);

        // 0. 首先根据ID查询是否已在数据库中存在
        BrokerMessage brokerMessage = messageStoreService.selectByMessageId(message.getMessageId());
        if(brokerMessage == null){
            // 1. 如果不存在, 则消息落库, tryCount默认值是0, 保证可靠性
            Date now = new Date();
            brokerMessage = new BrokerMessage();
            brokerMessage.setMessageId(message.getMessageId());
            brokerMessage.setStatus(BrokerMessageStatus.SENDING.getCode());
            brokerMessage.setNextRetry(DateUtils.addMinutes(now, BrokerMessageConst.TIMEOUT));
            brokerMessage.setCreateTime(now);
            brokerMessage.setUpdateTime(now);
            brokerMessage.setMessage(message);
            messageStoreService.insert(brokerMessage);
        }

        // 2. 发送消息
        sendKernel(message);
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
            RabbitTemplate rabbitTemplate = rabbitTemplateContainer.getRabbitTemplate(message);
            rabbitTemplate.convertAndSend(topic, routingKey, message, correlationData);
            log.info("#RabbitBrokerImpl.sendKernel# send to rabbitmq, messageId: {}", message.getMessageId());
        });
    }
}
