package com.jsonyao.cs.api;

import com.jsonyao.cs.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * 生产者
 */
public interface MessageProducer {

    /**
     * 发送消息
     * @param message
     * @throws MessageRunTimeException
     */
    void send(Message message) throws MessageRunTimeException;

    /**
     * 发送消息: 附带Callback回调
     * @param message
     * @param sendCallback
     * @throws MessageRunTimeException
     */
    void send(Message message, SendCallback sendCallback) throws MessageRunTimeException;

    /**
     * 批量发送消息
     * @param messages
     * @throws MessageRunTimeException
     */
    void send(List<Message> messages) throws MessageRunTimeException;
}
