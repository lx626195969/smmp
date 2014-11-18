package com.ddk.smmp.adapter.webservice.entity;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.webservice.entity.helper.Body;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 *
 */
public class BalanceRequest extends Body<BalanceRequest> {
	private static final long serialVersionUID = 4770273786217746416L;

	private String passWord;
	
	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public BalanceRequest() {
		super();
	}
	
	public BalanceRequest(String passWord) {
		super();
		this.passWord = passWord;
	}

	@Override
	public String toString() {
		return "BalanceRequest [passWord=" + passWord + "]";
	}

	@Override
	public String toJson(String key) {
		String result = null;
		try {
			result = AesUtil.encrypt(JSON.toJSONString(this), key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public BalanceRequest toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}
		
		return JSON.parseObject(json, BalanceRequest.class);
	}
}