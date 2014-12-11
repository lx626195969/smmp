package com.ddk.smmp.channel.gdydADC.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.dingyuan_http.handler.HongXing_HttpServer;
import com.ddk.smmp.channel.dingyuan_http.handler.SubmitThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class DingYuan_HttpClient extends Client {
	private static final long serialVersionUID = -3705225141297223091L;
	
	private static final Logger logger = Logger.getLogger(DingYuan_HttpClient.class);
	
	public DingYuan_HttpClient(Channel channel) {
		super();
		this.channel = channel;
		channel.setClient(this);
	}
	
	public SubmitThread submitThread = null;
	public HongXing_HttpServer dingYuan_HttpServer = null;
	
	@Override
	public void start() {
		try {
			ConstantUtils.updateChannelStatus(channel.getId(), 1);
			channel.setStatus(Channel.RUN_STATUS);
			
			if(null == submitThread){
				submitThread = new SubmitThread(channel);
				submitThread.start();
				ChannelLog.log(logger, "启动鼎元智业提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			
			if(null == dingYuan_HttpServer){
				dingYuan_HttpServer = new HongXing_HttpServer(channel);
				ChannelLog.log(logger, "启动鼎元智业报告和上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
				dingYuan_HttpServer.start();
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
			ChannelLog.log(logger, "停止鼎元智业http短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		if(null != dingYuan_HttpServer){
			try {
				dingYuan_HttpServer.stop();
				dingYuan_HttpServer = null;
				ChannelLog.log(logger, "停止鼎元智业提交处理线程.....", LevelUtils.getSucLevel(channel.getId()));
			} catch (Exception e) {
				ChannelLog.log(logger, "停止鼎元智业提交处理线程......" + e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			}
		}
	}
}