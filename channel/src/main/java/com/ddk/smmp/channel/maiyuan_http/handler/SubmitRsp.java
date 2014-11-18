package com.ddk.smmp.channel.maiyuan_http.handler;

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
@XmlRootElement(name = "returnsms")
@XmlType
public class SubmitRsp {
	@XmlElement
	private String returnstatus;
	@XmlElement
	private String message;
	@XmlElement
	private String remainpoint;
	@XmlElement
	private String taskID;
	@XmlElement
	private String successCounts;

	public String getReturnstatus() {
		return returnstatus;
	}

	public void setReturnstatus(String returnstatus) {
		this.returnstatus = returnstatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRemainpoint() {
		return remainpoint;
	}

	public void setRemainpoint(String remainpoint) {
		this.remainpoint = remainpoint;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public String getSuccessCounts() {
		return successCounts;
	}

	public void setSuccessCounts(String successCounts) {
		this.successCounts = successCounts;
	}

	@Override
	public String toString() {
		return "Returnsms [returnstatus=" + returnstatus + ", message="
				+ message + ", remainpoint=" + remainpoint + ", taskID="
				+ taskID + ", successCounts=" + successCounts + "]";
	}
}