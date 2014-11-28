package com.ddk.smmp.channel;

/**
 * @author leeson 2014年11月27日 上午9:55:37 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelThread implements Runnable {
	Client client = null;

	public ChannelThread(Client client) {
		super();
		this.client = client;
	}
	
	@Override
	public void run() {
		if(null != client){
			client.start();
		}
	}
}