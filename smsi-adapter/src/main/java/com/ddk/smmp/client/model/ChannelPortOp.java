package com.ddk.smmp.client.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author leeson 2014年7月25日 下午2:33:44 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelPortOp implements Serializable {
	private static final long serialVersionUID = 8787808049397568472L;

	private Integer id;
	private Integer portId;
	private String name;
	private Integer opType;

	LinkedList<ChannelPortOpParam> inputParamList = new LinkedList<ChannelPortOpParam>();
	LinkedList<ChannelPortOpParam> outputParamList = new LinkedList<ChannelPortOpParam>();
	
	public Integer getOpType() {
		return opType;
	}

	public void setOpType(Integer opType) {
		this.opType = opType;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPortId() {
		return portId;
	}

	public void setPortId(Integer portId) {
		this.portId = portId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LinkedList<ChannelPortOpParam> getInputParamList() {
		return inputParamList;
	}

	public void setInputParamList(LinkedList<ChannelPortOpParam> inputParamList) {
		this.inputParamList = inputParamList;
	}

	public LinkedList<ChannelPortOpParam> getOutputParamList() {
		return outputParamList;
	}

	public void setOutputParamList(LinkedList<ChannelPortOpParam> outputParamList) {
		this.outputParamList = outputParamList;
	}
}