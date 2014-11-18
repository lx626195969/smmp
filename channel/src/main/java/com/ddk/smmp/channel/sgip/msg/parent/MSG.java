package com.ddk.smmp.channel.sgip.msg.parent;

/**
 * 
 * @author leeson 2014-6-9 下午02:18:40 li_mr_ceo@163.com
 * 
 */
public abstract class MSG extends ByteData {
	public abstract boolean isRequest();

	public abstract boolean isResponse();

	public abstract void assignSequenceNumber(int nodeId);

	public abstract int getSequenceNumber1();

	public abstract int getSequenceNumber2();

	public abstract int getSequenceNumber3();

	public abstract boolean equals(Object object);

	public abstract String name();

	public abstract String dump();

	public long timeStamp = 0;
}