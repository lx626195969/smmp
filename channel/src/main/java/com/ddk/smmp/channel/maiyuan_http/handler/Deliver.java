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
public class Deliver {
	@XmlElement
	private String mobile;
	@XmlElement
	private String taskid;
	@XmlElement
	private String content;
	@XmlElement
	private String receivetime;
	@XmlElement
	private String extno;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReceivetime() {
		return receivetime;
	}

	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}

	public String getExtno() {
		return extno;
	}

	public void setExtno(String extno) {
		this.extno = extno;
	}

	@Override
	public String toString() {
		return "Deliver [mobile=" + mobile + ", taskid=" + taskid
				+ ", content=" + content + ", receivetime=" + receivetime
				+ ", extno=" + extno + "]";
	}
}