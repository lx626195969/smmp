package com.ddk.smmp.channel.cmpp._2.exception;

/**
 * 
 * @author leeson 2014-6-9 下午05:23:36 li_mr_ceo@163.com <br>
 *         setData异常
 */
public class MSGException extends SmsException {
	private static final long serialVersionUID = -2184183203286999926L;

	public MSGException() {
		super();
	}

	public MSGException(String s) {
		super(s);
	}

	public MSGException(Exception e) {
		super(e);
	}
}
