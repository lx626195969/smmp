package com.ddk.smmp.adapter;

/**
 * @author leeson 2014年7月19日 上午11:28:40 li_mr_ceo@163.com <br>
 * 
 */
public interface Server {
	final static int RUN_STATUS = 1;
	final static int STOP_STATUS = 0;

	public void start() throws Exception;

	public void stop() throws Exception;

	public int status();

	public int port();
	
	public void setPort(int port);
}
