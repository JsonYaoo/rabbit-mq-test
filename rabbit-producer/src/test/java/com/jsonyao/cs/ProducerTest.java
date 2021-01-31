package com.jsonyao.cs;

import com.jsonyao.cs.component.RabbitSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Hashtable;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerTest {

    @Autowired
    private RabbitSender rabbitSender;

    @Test
    public void testSender() throws InterruptedException {
        Hashtable<String, Object> properties = new Hashtable<>();
        properties.put("attr1", "12345");
        properties.put("attr2", "abcde");
        rabbitSender.send("hello rabbitmq!", properties);

        Thread.sleep(10000L);
    }
}
