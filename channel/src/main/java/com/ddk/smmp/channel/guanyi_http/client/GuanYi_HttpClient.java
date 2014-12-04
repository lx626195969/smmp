package com.ddk.smmp.channel.guanyi_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.guanyi_http.handler.GuanYi_HttpServer;
import com.ddk.smmp.channel.guanyi_http.handler.SubmitThread;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class GuanYi_HttpClient extends Client {
	private static final long serialVersionUID = -3705225141297223091L;
	
	private static final Logger logger = Logger.getLogger(GuanYi_HttpClient.class);
	
	public GuanYi_HttpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	public SubmitThread submitThread = null;
	public GuanYi_HttpServer guanyi_HttpServer = null;
	
	@Override
	public void start() {
		try {
			ConstantUtils.updateChannelStatus(channel.getId(), 1);
			channel.setStatus(Channel.RUN_STATUS);
			
			if(null == submitThread){
				submitThread = new SubmitThread(channel);
				submitThread.start();
				ChannelLog.log(logger, "启动冠艺短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			
			if(null == guanyi_HttpServer){
				guanyi_HttpServer = GuanYi_HttpServer.getInstance(channel);
				ChannelLog.log(logger, "启动冠艺报告和上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
				guanyi_HttpServer.start();
			}
		} catch (Exception e) {
			ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
			
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
		if(null != guanyi_HttpServer){
			try {
				guanyi_HttpServer.stop();
				guanyi_HttpServer = null;
				ChannelLog.log(logger, "停止冠艺短信提交处理线程.....", LevelUtils.getSucLevel(channel.getId()));
			} catch (Exception e) {
				ChannelLog.log(logger, "停止冠艺短信提交处理线程......" + e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			}
		}
	}
}