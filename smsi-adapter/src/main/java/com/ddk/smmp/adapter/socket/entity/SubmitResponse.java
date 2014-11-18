package com.ddk.smmp.adapter.socket.entity;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 *
 */
public class SubmitResponse extends Body<SubmitResponse> {
	private static final long serialVersionUID = 1014612843400398767L;
	
	private Integer code;
	private String batchNo;
	private String result;
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public SubmitResponse() {
		super();
	}

	public SubmitResponse(Integer code, String batchNo, String result) {
		super();
		this.code = code;
		this.batchNo = batchNo;
		this.result = result;
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
	public SubmitResponse toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}
		
		return JSON.parseObject(json, SubmitResponse.class);
	}
	
	@Override
	public String toString() {
		return "SubmitResponse [code=" + code + ", batchNo=" + batchNo + ", result=" + result + "]";
	}

	public static void main(String[] args){
		String key = DigestUtils.md5Hex("siookey");
		
		SubmitResponse request = new SubmitResponse(0, "201407091130250001", "");
		
		Msg msg = new Msg(Constants.SOCKET_COMMAND_SUBMIT_RESP, SeqUtil.generateSeq(), request.toJson(null));
		
		String json = msg.toJson();
		
		System.out.println(json);
		
		/*===========================================*/
		
		Msg msg2 = Msg.toObj(json);
		System.out.println(msg2);
		
		SubmitResponse submitResponse = new SubmitResponse();
		submitResponse = submitResponse.toObj(msg2.getBody(), key);
		
		System.out.println(submitResponse);
	}
}