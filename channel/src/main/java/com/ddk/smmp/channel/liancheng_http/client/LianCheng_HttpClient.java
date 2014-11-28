package com.ddk.smmp.channel.liancheng_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.jiaying_http.client.JiaYing_HttpClient;
import com.ddk.smmp.channel.liancheng_http.handler.DeliverThread;
import com.ddk.smmp.channel.liancheng_http.handler.ReportThread;
import com.ddk.smmp.channel.liancheng_http.handler.SubmitThread;
import com.ddk.smmp.jdbc.database.DatabaseException;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014年11月3日 下午3:59:20 li_mr_ceo@163.com <br>
 */
public class LianCheng_HttpClient implements Client {
	private static final long serialVersionUID = -4959290410169810270L;

	private static final Logger logger = Logger.getLogger(JiaYing_HttpClient.class);
	
	private Channel channel = null;

	public LianCheng_HttpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	ReportThread reportThread = null;
	DeliverThread deliverThread = null;
	
	@Override
	public void start() {
		try {
			if(null == submitThread){
				submitThread = new SubmitThread(channel);
				submitThread.start();
				ChannelLog.log(logger, "启动联诚智胜短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			if(null == reportThread){
				reportThread = new ReportThread(channel);
				reportThread.start();
				ChannelLog.log(logger, "启动联诚智胜报告处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			if(null == deliverThread){
				deliverThread = new DeliverThread(channel);
				deliverThread.start();
				ChannelLog.log(logger, "启动联诚智胜上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
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
		} catch (DatabaseException e) {
			ChannelLog.log(logger, e.getMessage(), LevelUtils.getErrLevel(channel.getId()), e.getCause());
		} catch (InterruptedException e) {
			if(null != submitThread){
				submitThread.stop_();
				submitThread = null;
				ChannelLog.log(logger, "停止联诚智胜短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			if(null != reportThread){
				reportThread.stop_();
				reportThread = null;
				ChannelLog.log(logger, "停止联诚智胜报告处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
			if(null != deliverThread){
				deliverThread.stop_();
				deliverThread = null;
				ChannelLog.log(logger, "停止联诚智胜上行处理线程......", LevelUtils.getSucLevel(channel.getId()));
			}
		}
	}
}
