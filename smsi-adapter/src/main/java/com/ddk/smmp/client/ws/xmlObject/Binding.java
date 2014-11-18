package com.ddk.smmp.client.ws.xmlObject;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午2:48:07 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "protocol", "portType" })
public class Binding extends XmlObj {
	private static final long serialVersionUID = 8887321438161443550L;

	private String protocol;
	private PortType portType;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public PortType getPortType() {
		return portType;
	}

	public void setPortType(PortType portType) {
		this.portType = portType;
	}
}