package com.ddk.smmp.adapter.socket.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 *
 */
public class SubmitRequest extends Body<SubmitRequest> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	private String[] phones;
	private String content;
	private String expId;
	private Integer productId;
	private String sendTime;
	
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

	public SubmitRequest(String[] phones, String content, String expId, Integer productId, String sendTime) {
		super();
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
		return "SubmitRequest [phones=" + Arrays.toString(phones)
				+ ", content=" + content + ", expId=" + expId + ", productId="
				+ productId + ", sendTime=" + sendTime + "]";
	}

	public static void main(String[] args){
		String key = "bJFCDwVeVvP93Nhga2bgXA==";
		
		String[] phones = new String[1];
		for(int i = 10001000; i <= 10001000; i ++){
			phones[i-10001000] = "152" + i;
		}
		String content = "大家下午好，今天下午6点在小会议室开会商讨上市方案，请提前安排好工作，准时参会。";
		Integer productId = 10;
		String sendTime = "20140709111850";
		String expId = "";
		SubmitRequest request = new SubmitRequest(phones, content, expId, productId, sendTime);
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_SUBMIT, SeqUtil.generateSeq(), request.toJson(key));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		SubmitRequest submitRequest = new SubmitRequest();
		submitRequest = submitRequest.toObj(msg2.getBody(), key);
		
		System.out.println(submitRequest);
	}
}