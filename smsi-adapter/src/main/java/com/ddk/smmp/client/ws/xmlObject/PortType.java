package com.ddk.smmp.client.ws.xmlObject;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午2:50:53 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "operationList" })
public class PortType extends XmlObj {
	private static final long serialVersionUID = 264692088860097435L;

	private List<Operation> operationList = new ArrayList<Operation>();

	public List<Operation> getOperationList() {
		return operationList;
	}

	public void setOperationList(List<Operation> operationList) {
		this.operationList = operationList;
	}
}