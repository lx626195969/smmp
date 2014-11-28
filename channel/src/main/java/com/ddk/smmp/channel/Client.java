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
}