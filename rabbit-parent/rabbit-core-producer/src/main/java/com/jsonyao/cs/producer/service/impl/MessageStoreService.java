package com.jsonyao.cs.producer.service.impl;

import com.jsonyao.cs.producer.constant.BrokerMessageStatus;
import com.jsonyao.cs.producer.entity.BrokerMessage;

import java.util.List;

/**
 * 消息存储服务
 */
public interface MessageStoreService {

    /**
     * 消息落库
     * @param brokerMessage
     * @return
     */
    int insert(BrokerMessage brokerMessage);

    /**
     * 根据ID查询消息
     * @param messageId
     * @return
     */
    BrokerMessage selectByMessageId(String messageId);

    /**
     * 成功消息投递
     * @param messageId
     */
    void succuess(String messageId);

    /**
     * 失败消息投递
     * @param messageId
     */
    void failure(String messageId);

    /**
     * 抓取超时消息
     * @param brokerMessageStatus
     * @return
     */
    List<BrokerMessage> fetchTimeOutMessage4Retry(BrokerMessageStatus brokerMessageStatus);

    /**
     * 更新重试次数
     * @param brokerMessageId
     * @return
     */
    int updateTryCount(String brokerMessageId);
}
