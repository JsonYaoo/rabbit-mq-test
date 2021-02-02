package com.jsonyao.cs;

import com.jsonyao.cs.api.Message;
import com.jsonyao.cs.api.MessageType;
import com.jsonyao.cs.producer.broker.ProducerClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    // 自定义ProducerClient, 由于使用了自动装配, 所以可以直接注入
    @Autowired
    private ProducerClient producerClient;

    @Test
    public void testProducerClient() throws InterruptedException {
        for(int i = 0; i < 1; i++){
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("name", "张三");
            attributes.put("age", "18");

            // 构造自定义Message
            Message message = new Message(UUID.randomUUID().toString(), "exchange-1", "springboot.abc", attributes, 0);
            message.setMessageType(MessageType.RAPID);
            producerClient.send(message);
        }

        Thread.sleep(Long.valueOf(Integer.MAX_VALUE));
    }
}
