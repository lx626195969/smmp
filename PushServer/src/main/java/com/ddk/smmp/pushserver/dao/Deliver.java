package com.ddk.smmp.pushserver.dao;

import com.alibaba.fastjson.annotation.JSONType;
import com.ddk.smmp.pushserver.jdbc.database.DataModel;

/**
 * @author leeson 2014年7月8日 上午10:48:33 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(ignores = { "id", "uid" })
public class Deliver extends DataModel {
	private static final long serialVersionUID = -1492890415055150550L;

	private Integer id;
	private Integer uid;
	private String phone;
	private String content;
	private String time;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Deliver(Integer id, Integer uid, String phone, String content, String time) {
		super();
		this.id = id;
		this.uid = uid;
		this.phone = phone;
		this.content = content;
		this.time = time;
	}

	public Deliver() {
		super();
	}

	@Override
	public String toString() {
		return "Deliver [id=" + id + ", uid=" + uid + ", phone=" + phone
				+ ", content=" + content + ", time=" + time + "]";
	}
}