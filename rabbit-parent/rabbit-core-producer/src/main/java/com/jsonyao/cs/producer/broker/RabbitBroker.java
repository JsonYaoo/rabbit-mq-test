package com.jsonyao.cs.producer.broker;

import com.jsonyao.cs.api.Message;

/**
 * RabbitMq消息发送接口
 */
public interface RabbitBroker {

    /**
     * 发送迅速消息(不落库)
     * @param message
     */
    void rapidSend(Message message);

    /**
     * 发送确认消息(不落库)
     * @param message
     */
    void confirmSend(Message message);

    /**
     * 发送可靠消息(落库)
     * @param message
     */
    void reliantSend(Message message);

    /**
     * 批量发送消息
     */
    void sendMessages();
}
