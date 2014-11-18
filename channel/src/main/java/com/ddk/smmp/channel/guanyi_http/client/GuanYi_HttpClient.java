package com.ddk.smmp.channel.guanyi_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.guanyi_http.handler.GuanYi_HttpServer;
import com.ddk.smmp.channel.guanyi_http.handler.SubmitThread;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class GuanYi_HttpClient implements Client {
	private static final long serialVersionUID = -3705225141297223091L;
	private static final Logger logger = Logger.getLogger(GuanYi_HttpClient.class);
	
	private Channel channel = null;

	public GuanYi_HttpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	GuanYi_HttpServer guanyi_HttpServer = null;
	
	@Override
	public void start() {
		if(null == submitThread){
			submitThread = new SubmitThread(channel);
			submitThread.start();
			ChannelLog.log(logger, "启动冠艺短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			new DbService(trans).updateChannelStatus(channel.getId(), 1);
			trans.commit();
		} catch (Exception ex) {
			ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			trans.rollback();
		} finally {
			trans.close();
		}
		
		channel.setStatus(Channel.RUN_STATUS);
		
		if(null == guanyi_HttpServer){
			guanyi_HttpServer = GuanYi_HttpServer.getInstance(channel);
			try {
				ChannelLog.log(logger, "启动冠艺报告和上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
				guanyi_HttpServer.start();
			} catch (Exception e) {
				ChannelLog.log(logger, "启动冠艺报告和上行处理线程失败......" + e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			
				DatabaseTransaction trans1 = new DatabaseTransaction(true);
				try {
					new DbService(trans1).updateChannelStatus(channel.getId(), 2);
					trans1.commit();
				} catch (Exception ex) {
					ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
					trans1.rollback();
				} finally {
					trans1.close();
				}
				
				channel.setStatus(Channel.STOP_STATUS);
			}
		}
	}

	@Override
	public void stop() {
		if(null != submitThread){
			submitThread.stop_();
			ChannelLog.log(logger, "停止http短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != guanyi_HttpServer){
			try {
				guanyi_HttpServer.stop();
				ChannelLog.log(logger, "停止冠艺短信提交处理线程.....", LevelUtils.getSucLevel(channel.getId()));
			} catch (Exception e) {
				ChannelLog.log(logger, "停止冠艺短信提交处理线程......" + e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			}
		}
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			new DbService(trans).updateChannelStatus(channel.getId(), 2);
			trans.commit();
		} catch (Exception ex) {
			ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()));
			trans.rollback();
		} finally {
			trans.close();
		}
		
		channel.setStatus(Channel.STOP_STATUS);
	}

	@Override
	public Integer status() {
		synchronized (channel) {
			return channel.getStatus();
		}
	}

	@Override
	public Channel getChannel() {
		synchronized (channel) {
			return this.channel;
		}
	}
}
