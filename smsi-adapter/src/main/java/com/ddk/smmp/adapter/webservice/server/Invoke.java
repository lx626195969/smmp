package com.ddk.smmp.adapter.webservice.server;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * anonymous complex type的 Java 类。
 * 
 * <p>
 * 以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="commandId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="uId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="body" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "commandId", "uId", "body" })
@XmlRootElement(name = "invoke")
public class Invoke {

	protected int commandId;
	@XmlElement(required = true)
	protected String uId;
	@XmlElement(required = true)
	protected String body;

	/**
	 * 获取commandId属性的值。
	 * 
	 */
	public int getCommandId() {
		return commandId;
	}

	/**
	 * 设置commandId属性的值。
	 * 
	 */
	public void setCommandId(int value) {
		this.commandId = value;
	}

	/**
	 * 获取uId属性的值。
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUId() {
		return uId;
	}

	/**
	 * 设置uId属性的值。
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUId(String value) {
		this.uId = value;
	}

	/**
	 * 获取body属性的值。
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBody() {
		return body;
	}

	/**
	 * 设置body属性的值。
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBody(String value) {
		this.body = value;
	}
}