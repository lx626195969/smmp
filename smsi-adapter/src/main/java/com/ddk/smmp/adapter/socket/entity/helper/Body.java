package com.ddk.smmp.adapter.socket.entity.helper;

import java.io.Serializable;

/**
 * @author leeson 2014年7月9日 上午9:26:26 li_mr_ceo@163.com <br>
 * 
 */
public abstract class Body<T> implements Serializable{
	private static final long serialVersionUID = -315443112169171747L;
	
	public abstract String toJson(String key);
	public abstract T toObj(String json, String key);
}