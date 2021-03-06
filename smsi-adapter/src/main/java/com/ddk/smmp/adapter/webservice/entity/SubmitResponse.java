package com.ddk.smmp.adapter.webservice.entity;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.webservice.entity.helper.Body;

/**
 * @author leeson 2014年7月10日 上午10:40:43 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitResponse extends Body<SubmitResponse> {
	private static final long serialVersionUID = 1213348746509355111L;

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
	
	public static void main(String[] args) {
		SubmitResponse response = new SubmitResponse(0, "2014073017460000001", "success");
		System.out.println(response.toJson(null));
	}
}