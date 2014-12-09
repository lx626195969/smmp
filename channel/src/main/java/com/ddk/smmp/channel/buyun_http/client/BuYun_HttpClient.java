package com.ddk.smmp.channel.buyun_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.buyun_http.handler.BuYun_HttpServer;
import com.ddk.smmp.channel.buyun_http.handler.SubmitThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class BuYun_HttpClient extends Client {
	private static final long serialVersionUID = -3705225141297223091L;
	
	private static final Logger logger = Logger.getLogger(BuYun_HttpClient.class);
	
	public BuYun_HttpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public SubmitThread submitThread = null;
	public BuYun_HttpServer buYun_HttpServer = null;
	
	@Override
	public void start() {
		try {
			ConstantUtils.updateChannelStatus(channel.getId(), 1);
			channel.setStatus(Channel.RUN_STATUS);
			
			if(null == submitThread){
				submitThread = new SubmitThread(channel);
				submitThread.start();
				ChannelLog.log(logger, "启动步云短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			
			if(null == buYun_HttpServer){
				buYun_HttpServer = BuYun_HttpServer.getInstance(channel);
				ChannelLog.log(logger, "启动步云报告和上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
				buYun_HttpServer.start();
			}
		} catch (Exception e) {
			ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e);
			
			ConstantUtils.updateChannelStatus(channel.getId(), 2);
			channel.setStatus(Channel.STOP_STATUS);
			
			this.stop();
		}
	}

	@Override
	public void stop() {
		if(null != submitThread){
			submitThread.stop_();
			submitThread = null;
			ChannelLog.log(logger, "停止http短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != buYun_HttpServer){
			try {
				buYun_HttpServer.stop();
				buYun_HttpServer = null;
				ChannelLog.log(logger, "停止步云短信提交处理线程.....", LevelUtils.getSucLevel(channel.getId()));
			} catch (Exception e) {
				ChannelLog.log(logger, "停止步云短信提交处理线程......" + e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e);
			}
		}
	}
}