package com.jsonyao.cs.api.exception;

/**
 * 消息运行时异常基类
 */
public class MessageRunTimeException extends RuntimeException {

    private static final long serialVersionUID = -796422246779770533L;

    public MessageRunTimeException() {
		super();
	}
	
	public MessageRunTimeException(String message) {
		super(message);
	}
	
	public MessageRunTimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public MessageRunTimeException(Throwable cause) {
		super(cause);
	}
}
