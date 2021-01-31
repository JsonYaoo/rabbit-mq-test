package com.jsonyao.cs.producer.broker;

import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.api.MessageProducer;
import com.jsonyao.cs.api.SendCallback;
import com.jsonyao.cs.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * Producer实现类
 */
public class ProducerClient implements MessageProducer {

    @Override
    public void send(Message message) throws MessageRunTimeException {

    }

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {

    }

    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {

    }
}
