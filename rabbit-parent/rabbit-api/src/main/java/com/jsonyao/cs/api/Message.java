package com.jsonyao.cs.api;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * API: Message
 */
@Data
public class Message implements Serializable {

    private static final long serialVersionUID = 8687721397232424855L;

    /**
     * 消息的唯一ID
     */
    private String messageId;

    /**
     * 消息的主题: 即Exchange
     */
    private String topic;

    /**
     * 消息的路由规则: 即RoutingKey
     */
    private String routingKey = "";

    /**
     * 消息的附加属性
     */
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * 延迟消息的参数配置
     */
    private Integer delayMills;

    /**
     * 消息类型: 默认为Confirm类型
     */
    private String messageType = MessageType.CONFIRM;

    /**
     * 构造函数: 无参
     */
    public Message() {

    }

    /**
     * 构造函数: 默认消息类型
     */
    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, Integer delayMills) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
    }

    /**
     * 构造函数: 全部参数
     */
    public Message(String messageId, String topic, String routingKey, Map<String, Object> attributes, Integer delayMills, String messageType) {
        this.messageId = messageId;
        this.topic = topic;
        this.routingKey = routingKey;
        this.attributes = attributes;
        this.delayMills = delayMills;
        this.messageType = messageType;
    }
}
