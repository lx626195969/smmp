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
@XmlRootElement(name = "MtPacket")
@XmlType
public class MtPacket {
	@XmlElement
	private String cpid;
	@XmlElement
	private String mid = "0";
	@XmlElement
	private Integer cpmid;
	@XmlElement
	private String mobile;
	@XmlElement
	private String port;
	@XmlElement
	private String msg;
	@XmlElement
	private String signature;
	@XmlElement
	private String timestamp;
	@XmlElement
	private String validtime = "0";
	@XmlElement
	private String reserve;

	public String getCpid() {
		return cpid;
	}

	public void setCpid(String cpid) {
		this.cpid = cpid;
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getValidtime() {
		return validtime;
	}

	public void setValidtime(String validtime) {
		this.validtime = validtime;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public MtPacket() {
		super();
	}

	public MtPacket(String cpid, String mid, Integer cpmid, String mobile,
			String port, String msg, String signature, String timestamp,
			String validtime, String reserve) {
		super();
		this.cpid = cpid;
		this.mid = mid;
		this.cpmid = cpmid;
		this.mobile = mobile;
		this.port = port;
		this.msg = msg;
		this.signature = signature;
		this.timestamp = timestamp;
		this.validtime = validtime;
		this.reserve = reserve;
	}

	@Override
	public String toString() {
		return "MtPacket [cpid=" + cpid + ", mid=" + mid + ", cpmid=" + cpmid
				+ ", mobile=" + mobile + ", port=" + port + ", msg=" + msg
				+ ", signature=" + signature + ", timestamp=" + timestamp
				+ ", validtime=" + validtime + "]";
	}
}