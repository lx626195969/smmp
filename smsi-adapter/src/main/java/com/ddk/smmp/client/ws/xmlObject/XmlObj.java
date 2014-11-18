package com.ddk.smmp.client.ws.xmlObject;

import java.io.Serializable;

/**
 * @author leeson 2014年7月23日 下午2:26:27 li_mr_ceo@163.com <br>
 * 
 */
public class XmlObj implements Serializable {
	private static final long serialVersionUID = -38997696587071881L;

	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}