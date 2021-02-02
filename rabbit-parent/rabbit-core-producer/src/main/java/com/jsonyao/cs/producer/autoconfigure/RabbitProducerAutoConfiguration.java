package com.jsonyao.cs.producer.autoconfigure;

import com.jsonyao.cs.task.annotation.EnableElasticJob;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ组件自动装配: 主要是为了扫描自定义组件, 交由Spring来管理
 */
@EnableElasticJob
@Configuration
@ComponentScan({"com.jsonyao.cs.producer.*"})
public class RabbitProducerAutoConfiguration {

}
