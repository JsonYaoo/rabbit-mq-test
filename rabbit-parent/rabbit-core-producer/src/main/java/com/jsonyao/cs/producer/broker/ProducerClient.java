package com.jsonyao.cs.producer.broker;

import com.google.common.base.Preconditions;
import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.api.MessageProducer;
import com.jsonyao.cs.api.MessageType;
import com.jsonyao.cs.api.SendCallback;
import com.jsonyao.cs.api.exception.MessageRunTimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Producer实现类
 */
@Component
public class ProducerClient implements MessageProducer {

    @Autowired
    private RabbitBroker rabbitBroker;

    @Override
    public void send(Message message) throws MessageRunTimeException {
        // Exchange校验
        Preconditions.checkNotNull(message.getTopic());

        // 根据类型发送消息
        String messageType = message.getMessageType();
        switch (messageType){
            case MessageType.RAPID:
                rabbitBroker.rapidSend(message);
                break;
            case MessageType.CONFIRM:
                rabbitBroker.confirmSend(message);
                break;
            case MessageType.RELIANT:
                rabbitBroker.reliantSend(message);
                break;
            default:
                break;
        }
    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {

    }

    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {

    }
}
