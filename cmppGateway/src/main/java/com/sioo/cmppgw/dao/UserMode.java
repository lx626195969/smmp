package com.sioo.cmppgw.dao;

/**
 * 
 * @author leeson 2014年8月22日 上午11:47:03 li_mr_ceo@163.com <br>
 * 
 */
public class UserMode {
	private int id;// 数据库对应记录ID
	private String uid;// 用户名
	private String pwd;// 数据库加密过的密码
	private String expandCode;//字码
	private int limit;// 限流
	private String bindIp;//绑定IP地址
	private int productId;// 产品ID

	public String getExpandCode() {
		return expandCode;
	}

	public void setExpandCode(String expandCode) {
		this.expandCode = expandCode;
	}

	public String getBindIp() {
		return bindIp;
	}

	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public UserMode() {
		super();
	}

	public UserMode(int id, String uid, String pwd, String expandCode, int limit, String bindIp, int productId) {
		super();
		this.id = id;
		this.uid = uid;
		this.pwd = pwd;
		this.expandCode = expandCode;
		this.limit = limit;
		this.bindIp = bindIp;
		this.productId = productId;
	}
}