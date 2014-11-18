package com.ddk.smmp.client.ws.xmlObject;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午2:25:44 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "portList" })
public class Service extends XmlObj {
	private static final long serialVersionUID = 6752887021971740355L;

	private List<Port> portList = new ArrayList<Port>();

	public List<Port> getPortList() {
		return portList;
	}

	public void setPortList(List<Port> portList) {
		this.portList = portList;
	}
}