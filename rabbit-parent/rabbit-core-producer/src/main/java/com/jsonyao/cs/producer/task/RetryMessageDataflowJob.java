package com.jsonyao.cs.producer.task;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.jsonyao.cs.producer.broker.RabbitBroker;
import com.jsonyao.cs.producer.constant.BrokerMessageStatus;
import com.jsonyao.cs.producer.entity.BrokerMessage;
import com.jsonyao.cs.producer.service.MessageStoreService;
import com.jsonyao.cs.task.annotation.ElasticJobConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 可靠性投递消息补偿任务
 */
@Component
@ElasticJobConfig(
        name = "com.jsonyao.cs.producer.task.RetryMessageDataflowJob",
        cron = "0/10 * * * * ?",
        description = "可靠性投递消息补偿任务",
        overwrite = true,
        shardingTotalCount = 1// 1张BrokerMessage表就用一个分片
)
@Slf4j
public class RetryMessageDataflowJob implements DataflowJob<BrokerMessage> {

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 3;

    @Autowired
    private MessageStoreService messageStoreService;

    @Autowired
    private RabbitBroker rabbitBroker;

    /**
     * 1. 抓取数据
     * @param shardingContext
     * @return
     */
    @Override
    public List<BrokerMessage> fetchData(ShardingContext shardingContext) {
        List<BrokerMessage> brokerMessages = messageStoreService.fetchTimeOutMessage4Retry(BrokerMessageStatus.SENDING);
        log.info("--------@@@@@ 抓取数据集合, 数量：{} @@@@@@-----------" , brokerMessages.size());
        return brokerMessages;
    }

    /**
     * 2. 处理数据
     * @param shardingContext
     * @param brokerMessages
     */
    @Override
    public void processData(ShardingContext shardingContext, List<BrokerMessage> brokerMessages) {
        brokerMessages.forEach(brokerMessage -> {
            String messageId = brokerMessage.getMessageId();
            if(brokerMessage.getTryCount() >= MAX_RETRY_COUNT){
                // 超过最大重试次数, 则失败消息投递
                messageStoreService.failure(messageId);
                log.warn(" -----消息设置为最终失败，消息ID: {} -------", messageId);
            }else {
                // 否则, 更新重试次数, 然后重新发送消息
                messageStoreService.updateTryCount(messageId);
                rabbitBroker.reliantSend(brokerMessage.getMessage());
            }
        });
    }
}
