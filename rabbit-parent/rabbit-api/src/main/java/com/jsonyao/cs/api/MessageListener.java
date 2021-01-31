package com.jsonyao.cs.api;

/**
 * 消费者
 */
public interface MessageListener {

    /**
     * 监听消息
     * @param message
     */
    void onMessage(Message message);
}
