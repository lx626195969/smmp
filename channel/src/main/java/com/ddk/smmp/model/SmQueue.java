package com.ddk.smmp.model;

import com.ddk.smmp.jdbc.database.DataModel;


/**
 * @author leeson 2014-6-12 上午10:18:13 li_mr_ceo@163.com <br>
 *         队列表中的部分字段
 */
public class SmQueue extends DataModel {
	private static final long serialVersionUID = -3841326008710293860L;

	private Integer id;// ID
	private String phone;// 号码
	private String content;// 内容
	private String sendCode;//发送号码
	private Integer num;//实际发送数量

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
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
	
	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public SmQueue(Integer id, String phone, String content, String sendCode, Integer num) {
		super();
		this.id = id;
		this.phone = phone;
		this.content = content;
		this.sendCode = sendCode;
		this.num = num;
	}

	public SmQueue() {
		super();
	}
}