package com.ddk.smmp.dao;

/**
 * @author leeson 2014年8月6日 下午4:41:16 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitVo {
	private int rid;
	private int seq;
	private int channelId;

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public SubmitVo(int rid, int seq, int channelId) {
		super();
		this.rid = rid;
		this.seq = seq;
		this.channelId = channelId;
	}
}