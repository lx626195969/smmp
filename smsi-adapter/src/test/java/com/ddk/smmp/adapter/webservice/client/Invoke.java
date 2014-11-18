package com.ddk.smmp.adapter.webservice.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * anonymous complex type�� Java �ࡣ
 * 
 * <p>
 * ����ģʽƬ��ָ�����ڴ����е�Ԥ�����ݡ�
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
	 * ��ȡcommandId���Ե�ֵ��
	 * 
	 */
	public int getCommandId() {
		return commandId;
	}

	/**
	 * ����commandId���Ե�ֵ��
	 * 
	 */
	public void setCommandId(int value) {
		this.commandId = value;
	}

	/**
	 * ��ȡuId���Ե�ֵ��
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getUId() {
		return uId;
	}

	/**
	 * ����uId���Ե�ֵ��
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setUId(String value) {
		this.uId = value;
	}

	/**
	 * ��ȡbody���Ե�ֵ��
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBody() {
		return body;
	}

	/**
	 * ����body���Ե�ֵ��
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBody(String value) {
		this.body = value;
	}

}
