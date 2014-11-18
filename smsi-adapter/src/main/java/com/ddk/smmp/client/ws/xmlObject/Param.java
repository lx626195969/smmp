package com.ddk.smmp.client.ws.xmlObject;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午3:30:04 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "type" })
public class Param extends XmlObj {
	private static final long serialVersionUID = 3567436746473928706L;

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}