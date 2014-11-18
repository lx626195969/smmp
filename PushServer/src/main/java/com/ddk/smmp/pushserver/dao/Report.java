package com.ddk.smmp.pushserver.dao;

import com.alibaba.fastjson.annotation.JSONType;
import com.ddk.smmp.pushserver.jdbc.database.DataModel;

/**
 * @author leeson 2014年7月8日 上午10:48:33 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(ignores = { "id", "uid", "tbName" })
public class Report extends DataModel {
	private static final long serialVersionUID = -1492890415055150550L;
	private Integer id;
	private Integer uid;
	private String batchNum;
	private String phone;
	private String state;
	private String time;
	private String tbName;

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

	public String getTbName() {
		return tbName;
	}

	public void setTbName(String tbName) {
		this.tbName = tbName;
	}

	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public Report(Integer id, Integer uid, String batchNum, String phone, String state, String time, String tbName) {
		super();
		this.id = id;
		this.uid = uid;
		this.batchNum = batchNum;
		this.phone = phone;
		this.state = state;
		this.time = time;
		this.tbName = tbName;
	}

	public Report() {
		super();
	}

	@Override
	public String toString() {
		return "Report [id=" + id + ", uid=" + uid + ", batchNum=" + batchNum
				+ ", phone=" + phone + ", state=" + state + ", time=" + time
				+ ", tbName=" + tbName + "]";
	}
}