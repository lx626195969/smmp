package com.ddk.smmp.dao;

/**
 * @author leeson 2014年8月6日 下午4:41:16 li_mr_ceo@163.com <br>
 * 
 */
public class SmtDelivVo {
	private String rid;
	private int num;
	private int cid;

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public SmtDelivVo(String rid, int num, int cid) {
		super();
		this.rid = rid;
		this.num = num;
		this.cid = cid;
	}
}