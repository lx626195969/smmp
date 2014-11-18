package com.ddk.smmp.channel.sgip.exception;

/**
 * 
 * @author leeson 2014-6-9 下午05:19:38 li_mr_ceo@163.com <br>
 *         在bytebuffer中没有足够的数据 异常
 */
public class NotEnoughDataInByteBufferException extends SmsException {
	private static final long serialVersionUID = 5084315197182138348L;
	private int available;
	private int expected;

	public NotEnoughDataInByteBufferException(int p_available, int p_expected) {
		super("Not enough data in byte buffer. " + "Expected " + p_expected + ", available: " + p_available + ".");
		available = p_available;
		expected = p_expected;
	}

	public NotEnoughDataInByteBufferException(String s) {
		super(s);
		available = 0;
		expected = 0;
	}

	public int getAvailable() {
		return available;
	}

	public int getExpected() {
		return expected;
	}
}