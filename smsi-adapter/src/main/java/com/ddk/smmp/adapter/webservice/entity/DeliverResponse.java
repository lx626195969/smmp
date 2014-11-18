package com.ddk.smmp.adapter.webservice.entity;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.model.Deliver;
import com.ddk.smmp.adapter.utils.AesUtil;
import com.ddk.smmp.adapter.webservice.entity.helper.Body;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverResponse extends Body<DeliverResponse> {
	private static final long serialVersionUID = 1014612843400398767L;

	private int num;
	private Deliver[] delivers;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public Deliver[] getDelivers() {
		return delivers;
	}

	public void setDelivers(Deliver[] delivers) {
		this.delivers = delivers;
	}

	public DeliverResponse() {
		super();
	}

	public DeliverResponse(int num, Deliver[] delivers) {
		super();
		this.num = num;
		this.delivers = delivers;
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
	public DeliverResponse toObj(String json, String key) {
		try {
			json = AesUtil.decrypt(json, key);
		} catch (Exception e) {
			return null;
		}

		return JSON.parseObject(json, DeliverResponse.class);
	}

	@Override
	public String toString() {
		return "DeliverResponse [num=" + num + ", delivers=" + Arrays.toString(delivers) + "]";
	}
}