package com.ddk.smmp.channel.cmpp._2.exception;

/**
 * 
 * @author leeson 2014-6-9 下午05:20:24 li_mr_ceo@163.com <br>
 *         短消息异常基类
 */
public class SmsException extends Exception {
	private static final long serialVersionUID = 3117037333965625264L;

	public SmsException() {
		super();
	}

	public SmsException(Exception e) {
		super(e);
	}

	public SmsException(String s) {
		super(s);
	}

	public SmsException(String s, Exception e) {
		super(s, e);
	}
}
