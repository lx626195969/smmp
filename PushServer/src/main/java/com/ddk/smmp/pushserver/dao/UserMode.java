package com.ddk.smmp.pushserver.dao;

/**
 * @author leeson 2014年7月18日 下午3:03:44 li_mr_ceo@163.com <br>
 * 
 */
public class UserMode {
	private int id;// 数据库对应记录ID
	private String userName;// 用户名
	private String pwd;// 数据库加密过的密码
	private String key;// 解密key
	private String host;// 接口绑定IP

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public UserMode(int id, String userName, String pwd, String key, String host) {
		super();
		this.id = id;
		this.userName = userName;
		this.pwd = pwd;
		this.key = key;
		this.host = host;
	}

	public UserMode() {
		super();
	}
}