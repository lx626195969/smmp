package com.ddk.smmp.channel;

import java.io.Serializable;

import org.apache.mina.core.service.IoConnector;

/**
 * @author leeson 2014年6月16日 上午11:55:24 li_mr_ceo@163.com <br>
 * 
 */
public abstract class Client implements Serializable {
	private static final long serialVersionUID = 8870989980897465733L;
	
	public IoConnector connector = null;
	public Channel channel = null;
	public abstract void start();
	public abstract void stop();
}