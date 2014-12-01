package com.ddk.smmp.dao;

/**
 * @author leeson 2014年8月6日 下午4:41:16 li_mr_ceo@163.com <br>
 * 
 */
public class MtVo {
	private int type;//直连1 http2
	private int channelId;
	private String port;
	private int spType;
	private String phone;
	private String content;
	private int index;
	private int totle;

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public int getSpType() {
		return spType;
	}

	public void setSpType(int spType) {
		this.spType = spType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getTotle() {
		return totle;
	}

	public void setTotle(int totle) {
		this.totle = totle;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public MtVo(int type, int channelId, String port, int spType, String phone, String content, int index, int totle) {
		super();
		this.type = type;
		this.channelId = channelId;
		this.port = port;
		this.spType = spType;
		this.phone = phone;
		this.content = content;
		this.index = index;
		this.totle = totle;
	}

	public MtVo(int type, int channelId, String phone, String content, String port) {
		super();
		this.type = type;
		this.channelId = channelId;
		this.phone = phone;
		this.content = content;
		this.port = port;
	}
}