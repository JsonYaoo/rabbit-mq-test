package com.jsonyao.cs.producer.constant;

/**
 * 消息的发送状态
 */
public enum BrokerMessageStatus {

	SENDING("0"),// 发送中: 开始发送~Broker没回复ACK
	SEND_OK("1"),// 发送成功: Broker已回复ACK
	SEND_FAIL("2"),// 发送失败: Broker没回复ACK 且 Confirm等待超时 且 大于重试次数
	SEND_FAIL_A_MOMENT("3");// 发送失败但需要等待: 比如遇到Broker磁盘满了, 或者I/O写不进去了
	
	private String code;
	
	private BrokerMessageStatus(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
}