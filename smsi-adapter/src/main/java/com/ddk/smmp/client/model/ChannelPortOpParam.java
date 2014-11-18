package com.ddk.smmp.client.model;

import java.io.Serializable;

/**
 * @author leeson 2014年7月25日 下午2:33:44 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelPortOpParam implements Serializable {
	private static final long serialVersionUID = -2160287477244643321L;

	private Integer id;
	private Integer opId;
	private String name;
	private String type;
	private int sortnum;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getOpId() {
		return opId;
	}

	public void setOpId(Integer opId) {
		this.opId = opId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSortnum() {
		return sortnum;
	}

	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}
}