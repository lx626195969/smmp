package com.sioo.cmppgw.dao;

/**
 * @author leeson 2014年8月25日 上午10:18:25 li_mr_ceo@163.com <br>
 * 
 */
public class RecordMode {
	private int uid;
	private int rid;
	private String msgId;
	private int sort;
	private int totle;
	private String phone;
	private String srcId;
	
	private int id;
	private String time;
	private String state;
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}
	
	public int getTotle() {
		return totle;
	}

	public void setTotle(int totle) {
		this.totle = totle;
	}

	public RecordMode(int uid, int rid, String msgId, int sort, int totle, String phone, String srcId) {
		super();
		this.uid = uid;
		this.rid = rid;
		this.msgId = msgId;
		this.sort = sort;
		this.totle = totle;
		this.phone = phone;
		this.srcId = srcId;
	}

	public RecordMode(int id, int uid, String msgId, String state, String time, String phone, String srcId) {
		super();
		this.uid = uid;
		this.msgId = msgId;
		this.id = id;
		this.state = state;
		this.time = time;
		this.phone = phone;
		this.srcId = srcId;
	}

	public RecordMode() {
		super();
	}

	@Override
	public String toString() {
		return "RecordMode [uid=" + uid + ", rid=" + rid + ", msgId=" + msgId
				+ ", sort=" + sort + ", totle=" + totle + ", phone=" + phone
				+ ", srcId=" + srcId + ", id=" + id + ", time=" + time
				+ ", state=" + state + "]";
	}
}