package com.jsonyao.cs.task.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Zookeeper配置属性类
 */
@ConfigurationProperties(prefix = "elastic.job.zk")
@Data
public class JobZookeeperProperties {

    /**
     * 命令空间
     */
    private String namespace;

    /**
     * Zookeeper服务器列表
     */
    private String serverLists;

    /**
     * 最大重试次数
     */
    private int maxRetries = 3;

    /**
     * 连接超时时间
     */
    private int connectionTimeoutMilliseconds = 15000;

    /**
     * 会话超时时间
     */
    private int sessionTimeoutMilliseconds = 60000;

    /**
     * 初始等待重试时间
     */
    private int baseSleepTimeMilliseconds = 1000;

    /**
     * 最大等待重试时间
     */
    private int maxSleepTimeMilliseconds = 3000;

    /**
     * 权限令牌
     */
    private String digest = "";
}
