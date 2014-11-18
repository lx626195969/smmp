package com.ddk.smmp.channel;

import java.io.Serializable;

/**
 * @author leeson 2014年6月16日 上午11:55:24 li_mr_ceo@163.com <br>
 * 
 */
public interface Client extends Serializable {
	
	/**
	 * 启动通道
	 */
	public void start();

	/**
	 * 停止通道
	 */
	public void stop();

	/**
	 * 获取运行状态1运行 2停止 3重连
	 * 
	 * @return
	 */
	public Integer status();
	
	/**
	 * 获取通道
	 * 
	 * @return
	 */
	public Channel getChannel();
}