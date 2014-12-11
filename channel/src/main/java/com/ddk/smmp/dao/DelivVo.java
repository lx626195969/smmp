package com.ddk.smmp.dao;

/**
 * @author leeson 2014年8月6日 下午4:41:16 li_mr_ceo@163.com <br>
 * 
 */
public class DelivVo {
	private long msgId;
	private int channelId;
	private String state;
	private String time;

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public DelivVo(long msgId, int channelId, String state, String time) {
		super();
		this.msgId = msgId;
		this.channelId = channelId;
		this.state = state;
		this.time = time;
	}

	@Override
	public String toString() {
		return "DelivVo [msgId=" + msgId + ", channelId=" + channelId + ", state=" + state + ", time=" + time + "]";
	}
}