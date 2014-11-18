package com.ddk.smmp.client.ws.xmlObject;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午2:45:49 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "nameSpace", "prefix", "serviceList" })
public class Def extends XmlObj {
	private static final long serialVersionUID = 5767113032366382041L;

	private String nameSpace;
	private String prefix;
	private List<Service> serviceList = new ArrayList<Service>();

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public List<Service> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<Service> serviceList) {
		this.serviceList = serviceList;
	}
}