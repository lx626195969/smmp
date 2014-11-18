package com.ddk.smmp.client.ws.xmlObject;

import java.util.LinkedList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONType;

/**
 * @author leeson 2014年7月23日 下午2:51:45 li_mr_ceo@163.com <br>
 * 
 */
@JSONType(orders = { "name", "url", "input", "output" })
public class Operation extends XmlObj {
	private static final long serialVersionUID = -3354835242082776004L;

	private String url;
	private List<Param> input = new LinkedList<Param>();
	private List<Param> output = new LinkedList<Param>();

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Param> getInput() {
		return input;
	}

	public void setInput(List<Param> input) {
		this.input = input;
	}

	public List<Param> getOutput() {
		return output;
	}

	public void setOutput(List<Param> output) {
		this.output = output;
	}
}