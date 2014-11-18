package com.ddk.smmp.adapter.webservice.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.webservice.entity.helper.Body;

/**
 * @author leeson 2014年7月10日 上午10:40:43 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitRequest extends Body<SubmitRequest> {
	private static final long serialVersionUID = -6895907760795283756L;
	
	private String passWord;
	private String[] phones;
	private String content;
	private String expId;
	private Integer productId;
	private String sendTime;

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String[] getPhones() {
		return phones;
	}

	public void setPhones(String[] phones) {
		this.phones = phones;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExpId() {
		return expId;
	}

	public void setExpId(String expId) {
		this.expId = expId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
	public SubmitRequest() {
		super();
	}

	public SubmitRequest(String passWord, String[] phones, String content,
			String expId, Integer productId, String sendTime) {
		super();
		this.passWord = passWord;
		this.phones = phones;
		this.content = content;
		this.expId = expId;
		this.productId = productId;
		this.sendTime = sendTime;
	}

	@Override
	public String toJson(String key) {
		if(null == key){
			return JSON.toJSONString(this);
		}
		
		String result = null;
		try {
			result = AesUtil.encrypt(JSON.toJSONString(this), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public SubmitRequest toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}
		
		return JSON.parseObject(json, SubmitRequest.class);
	}

	@Override
	public String toString() {
		return "SubmitRequest [passWord=" + passWord + ", phones="
				+ Arrays.toString(phones) + ", content=" + content + ", expId="
				+ expId + ", productId=" + productId + ", sendTime=" + sendTime
				+ "]";
	}

	public static void main(String[] args) {
		String[] phones = new String[1];
		for(int i = 10001000; i <= 10001000; i ++){
			phones[i-10001000] = "152" + i;
		}
		String password = "test";
		String content = "测试短信";
		Integer productId = 10;
		String sendTime = "2014-07-09 11:00";
		String expId = "";
		
		SubmitRequest request = new SubmitRequest(password, phones, content, expId, productId, sendTime);
		System.out.println(request.toJson(null));
	}
}