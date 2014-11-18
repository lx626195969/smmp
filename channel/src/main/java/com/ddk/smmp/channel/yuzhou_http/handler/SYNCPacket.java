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
@XmlRootElement(name = "SYNCPacket")
@XmlType
public class SYNCPacket {
	@XmlElement
	private String mid;
	@XmlElement
	private Integer cpmid;
	@XmlElement
	private String mobile;
	@XmlElement
	private String port;
	@XmlElement
	private String msg;
	@XmlElement
	private String area;
	@XmlElement
	private String city;
	@XmlElement
	private Integer type;
	@XmlElement
	private Integer channel;
	@XmlElement
	private String reserve;

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

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	@Override
	public String toString() {
		return "SYNCPacket [mid=" + mid + ", cpmid=" + cpmid + ", mobile="
				+ mobile + ", port=" + port + ", msg=" + msg + ", area=" + area
				+ ", city=" + city + ", type=" + type + ", channel=" + channel
				+ ", reserve=" + reserve + "]";
	}
}