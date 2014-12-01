package com.ddk.smmp.channel.sgip.client;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.sgip.handler.DeliverThread;
import com.ddk.smmp.channel.sgip.handler.ReportThread;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.Bind;
import com.ddk.smmp.channel.sgip.msg.BindResp;
import com.ddk.smmp.channel.sgip.msg.Deliver;
import com.ddk.smmp.channel.sgip.msg.DeliverResp;
import com.ddk.smmp.channel.sgip.msg.Report;
import com.ddk.smmp.channel.sgip.msg.ReportResp;
import com.ddk.smmp.channel.sgip.msg.parent.SgipMSG;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014年6月26日 下午4:44:45 li_mr_ceo@163.com <br>
 *
 */
public class SgipListenerIoHandler extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(SgipListenerIoHandler.class);
	
	Channel channel = null;
	
	DeliverThread deliverThread = null;
	ReportThread reportThread = null;
	
	public SgipListenerIoHandler(Channel channel) {
		this.channel = channel;
		
		deliverThread = new DeliverThread(channel);
		deliverThread.start();
		ChannelCacheUtil.put("child_thread_" + channel.getId(), "deliverThread", deliverThread);
		
		reportThread = new ReportThread(channel);
		reportThread.start();
		ChannelCacheUtil.put("child_thread_" + channel.getId(), "reportThread", reportThread);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		if (!(cause instanceof IOException)) {
			ChannelLog.log(logger, "Exception: " + cause.getMessage(), LevelUtils.getErrLevel(channel.getId()), cause);
		} else {
			ChannelLog.log(logger, "I/O error: " + cause.getMessage(), LevelUtils.getErrLevel(channel.getId()), cause);
		}
		session.close(true);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		ChannelLog.log(logger, "Session " + session.getId() + " is opened", LevelUtils.getSucLevel(channel.getId()));
		session.resumeRead();//恢复读取
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		ChannelLog.log(logger, "Creation of session " + session.getId(), LevelUtils.getSucLevel(channel.getId()));
		session.suspendRead();//暂停读取
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		ChannelLog.log(logger, session.getId() + "> Session closed", LevelUtils.getSucLevel(channel.getId()));
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		SgipMSG msg = (SgipMSG) message;
		switch (msg.header.getCommandId()) {
		case SgipConstant.CMD_BIND:
			Bind bind = (Bind)msg;
			BindResp bindResp = (BindResp)bind.getResponse();
			bindResp.setResult(0);
			session.write(bindResp);
			
			break;
		case SgipConstant.CMD_DELIVER:
			Deliver deliver = (Deliver) msg;
			
			deliverThread.queue.offer(deliver);//将数据加入处理队列
			
			DeliverResp deliverResp = (DeliverResp)deliver.getResponse();
			deliverResp.setResult(0);
			session.write(deliverResp);
			
			break;
		case SgipConstant.CMD_REPORT:
			Report report = (Report) msg;
			report.setReceiveDate(new Date(System.currentTimeMillis()));
			reportThread.queue.offer(report);//将数据加入处理队列
			
			ReportResp reportResp = (ReportResp)report.getResponse();
			reportResp.setResult(0);
			session.write(reportResp);
			
			break;
		default:
			ChannelLog.log(logger, "Unexpected MSG received! MSG Header: " + msg.header.getData().getHexDump(), LevelUtils.getErrLevel(channel.getId()));
			session.close(true);
			break;
		}
	}
}