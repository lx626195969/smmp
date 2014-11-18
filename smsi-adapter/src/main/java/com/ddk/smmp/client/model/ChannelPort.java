package com.ddk.smmp.client.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author leeson 2014年7月25日 下午2:30:24 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelPort implements Serializable {
	private static final long serialVersionUID = -5464968714780460164L;

	private Integer id;
	private Integer channelId;
	private String protocol;
	private String soapAction;

	LinkedList<ChannelPortOp> operationList = new LinkedList<ChannelPortOp>();
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public LinkedList<ChannelPortOp> getOperationList() {
		return operationList;
	}

	public void setOperationList(LinkedList<ChannelPortOp> operationList) {
		this.operationList = operationList;
	}
}