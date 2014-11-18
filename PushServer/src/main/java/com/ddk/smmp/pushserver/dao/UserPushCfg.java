package com.ddk.smmp.pushserver.dao;

import java.io.Serializable;

/**
 * @author leeson 2014年11月6日 下午3:17:19 li_mr_ceo@163.com <br>
 * 
 */
public class UserPushCfg implements Serializable {
	private static final long serialVersionUID = 8648201406619965453L;

	private Integer id;
	private Integer userId;
	private String dlvUrl;
	private String rptUrl;
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getDlvUrl() {
		return dlvUrl;
	}

	public void setDlvUrl(String dlvUrl) {
		this.dlvUrl = dlvUrl;
	}

	public String getRptUrl() {
		return rptUrl;
	}

	public void setRptUrl(String rptUrl) {
		this.rptUrl = rptUrl;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public UserPushCfg(Integer id, Integer userId, String dlvUrl, String rptUrl, Integer status) {
		super();
		this.id = id;
		this.userId = userId;
		this.dlvUrl = dlvUrl;
		this.rptUrl = rptUrl;
		this.status = status;
	}

	public UserPushCfg() {
		super();
	}
}