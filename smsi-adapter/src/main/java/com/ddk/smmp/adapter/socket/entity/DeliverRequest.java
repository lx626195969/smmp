package com.ddk.smmp.adapter.socket.entity;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.socket.entity.helper.Body;

/**
 * 
 * @author leeson 2014年7月9日 上午11:10:39 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverRequest extends Body<DeliverRequest> {
	private static final long serialVersionUID = 1014612843400398767L;

	public DeliverRequest() {
		super();
	}

	@Override
	public String toJson(String key) {
		return "{}";
	}

	@Override
	public DeliverRequest toObj(String json, String key) {
		return JSON.parseObject(json, DeliverRequest.class);
	}
}