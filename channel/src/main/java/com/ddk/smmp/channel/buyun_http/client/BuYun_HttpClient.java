package com.ddk.smmp.channel.buyun_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.buyun_http.handler.BuYun_HttpServer;
import com.ddk.smmp.channel.buyun_http.handler.SubmitThread;
import com.ddk.smmp.jdbc.database.DatabaseException;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class BuYun_HttpClient implements Client {
	private static final long serialVersionUID = -3705225141297223091L;
	
	private static final Logger logger = Logger.getLogger(BuYun_HttpClient.class);
	
	private Channel channel = null;

	public BuYun_HttpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	BuYun_HttpServer buYun_HttpServer = null;
	
	@Override
	public void start() {
			try {
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
				
				//添加阻塞
				while (true) {
					Thread.sleep(10 * 1000);
				}
			} catch (DatabaseException e1) {
				ChannelLog.log(logger, e1.getMessage(), LevelUtils.getErrLevel(channel.getId()), e1.getCause());
			} catch (InterruptedException e1) {
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
						ChannelLog.log(logger, "停止步云短信提交处理线程......" + e.getMessage(), LevelUtils.getErrLevel(channel.getId()));
					}
				}
			} catch (Exception e1) {
				ChannelLog.log(logger, e1.getMessage(), LevelUtils.getErrLevel(channel.getId()), e1.getCause());
			}
	}
}