package com.ddk.smmp.adapter.model;

import com.alibaba.fastjson.annotation.JSONType;
import com.ddk.smmp.adapter.jdbc.database.DataModel;

/**
 * @author leeson 2014年7月8日 上午10:48:33 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(ignores = { "id" })
public class Report extends DataModel {
	private static final long serialVersionUID = -1492890415055150550L;
	
	private Integer id;
	private String batchNum;
	private String phone;
	private String state;
	private String time;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Report(Integer id, String batchNum, String phone, String state, String time) {
		super();
		this.id = id;
		this.batchNum = batchNum;
		this.phone = phone;
		this.state = state;
		this.time = time;
	}

	public Report(String batchNum, String phone, String state, String time) {
		super();
		this.batchNum = batchNum;
		this.phone = phone;
		this.state = state;
		this.time = time;
	}

	public Report() {
		super();
	}

	@Override
	public String toString() {
		return "Report [batchNum=" + batchNum + ", phone=" + phone + ", state=" + state + ", time=" + time + "]";
	}
}