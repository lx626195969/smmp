package com.ddk.smmp.adapter.webservice.entity.helper;

import java.io.Serializable;

/**
 * 
 * @author leeson 2014年7月10日 上午10:42:19 li_mr_ceo@163.com <br>
 *
 * @param <T>
 */
public abstract class Body<T> implements Serializable{
	private static final long serialVersionUID = -4179668729452485082L;
	
	public abstract String toJson(String key);
	public abstract T toObj(String json, String key);
}