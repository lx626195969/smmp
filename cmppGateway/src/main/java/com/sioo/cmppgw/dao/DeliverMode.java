package com.sioo.cmppgw.dao;

/**
 * @author leeson 2014年8月25日 下午3:42:17 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverMode {
	private int id;
	private int uid;
	private String content;
	private String phone;
	private String srcId;
	private int index;
	private int totle;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

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

	public DeliverMode() {
		super();
	}

	public DeliverMode(int id, int uid, String content, String phone,
			String srcId, int index, int totle) {
		super();
		this.id = id;
		this.uid = uid;
		this.content = content;
		this.phone = phone;
		this.srcId = srcId;
		this.index = index;
		this.totle = totle;
	}
}