package com.jsonyao.cs.api.exception;

/**
 * 消息异常基类
 */
public class MessageException extends Exception {

	private static final long serialVersionUID = 3520394272859413549L;

	public MessageException() {
		super();
	}
	
	public MessageException(String message) {
		super(message);
	}
	
	public MessageException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MessageException(Throwable cause) {
		super(cause);
	}
	
}
