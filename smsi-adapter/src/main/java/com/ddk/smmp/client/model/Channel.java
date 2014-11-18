package com.ddk.smmp.client.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author leeson 2014年7月25日 下午2:28:24 li_mr_ceo@163.com <br>
 * 
 */
public class Channel implements Serializable {
	private static final long serialVersionUID = 6879890723936479178L;

	private Integer id;
	private String name;
	private String address;
	private String namespace;

	LinkedList<ChannelPort> portList = new LinkedList<ChannelPort>();
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public LinkedList<ChannelPort> getPortList() {
		return portList;
	}

	public void setPortList(LinkedList<ChannelPort> portList) {
		this.portList = portList;
	}
}