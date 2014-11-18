package com.ddk.smmp.client.ws.xmlObject;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午2:47:29 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "address", "binding" })
public class Port extends XmlObj {
	private static final long serialVersionUID = 1918148954058060645L;

	private String address;

	private Binding binding;

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}
}