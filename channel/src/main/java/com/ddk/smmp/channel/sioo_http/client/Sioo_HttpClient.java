package com.ddk.smmp.channel.sioo_http.client;

import org.apache.log4j.Logger;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.Client;
import com.ddk.smmp.channel.sioo_http.handler.DeliverThread;
import com.ddk.smmp.channel.sioo_http.handler.ReportThread;
import com.ddk.smmp.channel.sioo_http.handler.SubmitThread;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;

/**
 * @author leeson 2014年9月1日 上午10:23:23 li_mr_ceo@163.com <br>
 * 
 */
public class Sioo_HttpClient implements Client {
	private static final long serialVersionUID = -3705225141297223091L;
	private static final Logger logger = Logger.getLogger(Sioo_HttpClient.class);
	
	private Channel channel = null;

	public Sioo_HttpClient(Channel channel) {
		super();
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	ReportThread reportThread = null;
	DeliverThread deliverThread = null;
	
	@Override
	public void start() {
		if(null == submitThread){
			submitThread = new SubmitThread(channel);
			submitThread.start();
			logger.info("启动希奥短信提交处理线程......");
		}
		if(null == reportThread){
			reportThread = new ReportThread(channel);
			reportThread.start();
			logger.info("启动希奥报告处理线程......");
		}
		if(null == deliverThread){
			deliverThread = new DeliverThread(channel);
			deliverThread.start();
			logger.info("启动希奥上行处理线程......");
		}
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			new DbService(trans).updateChannelStatus(channel.getId(), 1);
			trans.commit();
		} catch (Exception ex) {
			trans.rollback();
		} finally {
			trans.close();
		}
		
		channel.setStatus(Channel.RUN_STATUS);
	}

	@Override
	public void stop() {
		if(null != submitThread){
			submitThread.stop_();
			logger.info("停止希奥短信提交处理线程......");
		}
		if(null != reportThread){
			reportThread.stop_();
			logger.info("停止希奥报告处理线程......");
		}
		if(null != deliverThread){
			deliverThread.stop_();
			logger.info("停止希奥上行处理线程......");
		}
		
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			new DbService(trans).updateChannelStatus(channel.getId(), 2);
			trans.commit();
		} catch (Exception ex) {
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
