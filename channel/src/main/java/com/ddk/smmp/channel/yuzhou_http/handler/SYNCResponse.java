package com.ddk.smmp.channel.yuzhou_http.handler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author leeson 2014年10月22日 下午12:02:25 li_mr_ceo@163.com <br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SYNCResponse")
@XmlType
public class SYNCResponse {
	@XmlElement
	private String mid;
	@XmlElement
	private Integer cpmid;
	@XmlElement
	private String result;

	public SYNCResponse(String mid, Integer cpmid, String result) {
		super();
		this.mid = mid;
		this.cpmid = cpmid;
		this.result = result;
	}

	public SYNCResponse() {
		super();
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public Integer getCpmid() {
		return cpmid;
	}

	public void setCpmid(Integer cpmid) {
		this.cpmid = cpmid;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "MtResponse [mid=" + mid + ", cpmid=" + cpmid + ", result="
				+ result + "]";
	}
}