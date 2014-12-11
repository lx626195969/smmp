package com.ddk.smmp.dao;

/**
 * @author leeson 2014年8月6日 下午4:41:16 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitRspVo {
	private int seq;
	private int rid;
	private long msgId;
	private int channelId;
	private String state;

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

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

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public SubmitRspVo(int seq, int rid, long msgId, int channelId, String state) {
		super();
		this.seq = seq;
		this.rid = rid;
		this.msgId = msgId;
		this.channelId = channelId;
		this.state = state;
	}

	@Override
	public String toString() {
		return "SubmitRspVo [seq=" + seq + ", rid=" + rid + ", msgId=" + msgId + ", channelId=" + channelId + ", state=" + state + "]";
	}
}