package com.ddk.smmp.adapter.socket.entity.helper;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * @author leeson 2014年7月8日 上午10:15:53 li_mr_ceo@163.com <br>
 * 
 */
public class Msg implements Serializable {
	private static final long serialVersionUID = -3641311271558614079L;

	protected Integer commandId;
	protected Integer seq;
	protected String body;

	public Integer getCommandId() {
		return commandId;
	}

	public void setCommandId(Integer commandId) {
		this.commandId = commandId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Msg() {
		super();
	}

	public Msg(Integer commandId, Integer seq, String body) {
		super();
		this.commandId = commandId;
		this.seq = seq;
		this.body = body;
	}

	@Override
	public String toString() {
		return "[commandId=" + commandId + ", seq=" + seq + "]";
	}

	public String toJson(){
		return JSON.toJSONString(this);
	}
	
	public static Msg toObj(String json){
		try {
			return JSON.parseObject(json, Msg.class);
		} catch (Exception e) {
			return null;
		}
	}
}