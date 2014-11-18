package com.ddk.smmp.adapter.http.entity.helper;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author leeson 2014年7月10日 上午10:22:28 li_mr_ceo@163.com <br>
 * 
 */
public class Msg implements Serializable {
	private static final long serialVersionUID = -3641311271558614079L;

	protected String uId;
	protected String body;

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Msg() {
		super();
	}

	public Msg(String uId, String body) {
		super();
		this.uId = uId;
		this.body = body;
	}
	
	

	@Override
	public String toString() {
		return "[uId=" + uId + ", body=" + body + "]";
	}

	public String toJson() {
		return JSON.toJSONString(this, true);
	}

	public static Msg toObj(String json) {
		try {
			return JSON.parseObject(json, Msg.class);
		} catch (Exception e) {
			return null;
		}
	}
}