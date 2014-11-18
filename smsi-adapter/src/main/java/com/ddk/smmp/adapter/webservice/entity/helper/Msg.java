package com.ddk.smmp.adapter.webservice.entity.helper;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;
import com.ddk.smmp.adapter.utils.Constants;

/**
 * 
 * @author leeson 2014年7月10日 上午10:22:28 li_mr_ceo@163.com <br>
 * 
 */
public class Msg implements Serializable {
	private static final long serialVersionUID = -3641311271558614079L;

	protected int commandId = Constants.WEBSERVICE_COMMAND_ERROR;
	protected String body;

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
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

	public Msg(int commandId, String body) {
		super();
		this.commandId = commandId;
		this.body = body;
	}

	@Override
	public String toString() {
		return "[commandId=" + commandId + ", body=" + body + "]";
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