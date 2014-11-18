package com.ddk.smmp.channel.maiyuan_http.handler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author leeson 2014年10月22日 下午12:02:25 li_mr_ceo@163.com <br>
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class ReportErr {
	@XmlElement
	private String error;
	@XmlElement
	private String remark;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "ReportErr [error=" + error + ", remark=" + remark + "]";
	}
}