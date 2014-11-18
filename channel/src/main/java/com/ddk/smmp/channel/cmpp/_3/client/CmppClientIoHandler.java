package com.ddk.smmp.channel.cmpp._3.client;

import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.log4j.Logger;



import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.cmpp._3.handler.ActiveTestThread;
import com.ddk.smmp.channel.cmpp._3.handler.DeliverThread;
import com.ddk.smmp.channel.cmpp._3.handler.SubmitResponseThread;
import com.ddk.smmp.channel.cmpp._3.handler.SubmitThread;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.ActiveTest;
import com.ddk.smmp.channel.cmpp._3.msg.ActiveTestResp;
import com.ddk.smmp.channel.cmpp._3.msg.Connect;
import com.ddk.smmp.channel.cmpp._3.msg.ConnectResp;
import com.ddk.smmp.channel.cmpp._3.msg.Deliver;
import com.ddk.smmp.channel.cmpp._3.msg.DeliverResp;
import com.ddk.smmp.channel.cmpp._3.msg.SubmitResp;
import com.ddk.smmp.channel.cmpp._3.msg.Terminate;
import com.ddk.smmp.channel.cmpp._3.msg.TerminateResp;
import com.ddk.smmp.channel.cmpp._3.msg.parent.CmppMSG;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014-6-10 上午11:22:44 li_mr_ceo@163.com <br>
 *         客户端消息处理类
 */
public class CmppClientIoHandler extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(CmppClientIoHandler.class);
	
	private Channel channel = null;
	public CmppClientIoHandler(Channel channel) {
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	SubmitResponseThread submitResponseThread = null;
	ActiveTestThread heartbeatThread = null;
	DeliverThread deliverThread = null;
	
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
		
		channel.setSession(session);
		
		// 向服务端发起Connect消息
		Connect request = new Connect(CmppConstant.TRANSMITTER);
		request.setSrcAddr(channel.getCompanyCode());
		request.setSharedSecret(channel.getPassword());
		request.setTimeStamp(request.genTimeStamp());
		request.setAuthClient(request.genAuthClient());
		request.setVersion(48);
		request.assignSequenceNumber();
		session.write(request);
		
		// 启动ActiveTest链路心跳检测Thread
		heartbeatThread = new ActiveTestThread(session, channel.getId());
		heartbeatThread.start();
		//将启动的线程加入缓存管理
		ChannelCacheUtil.put("thread_" + channel.getId(), "heartbeatThread", heartbeatThread);
		
		session.resumeRead();//恢复读取
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		ChannelLog.log(logger, "Creation of session " + session.getId(), LevelUtils.getSucLevel(channel.getId()));
		
		session.suspendRead();//暂停读取
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		channel.setStatus(Channel.STOP_STATUS);
		
		session.removeAttribute("isSend");//移除可发送标识
				
		//更改状态为停止
		DatabaseTransaction trans = new DatabaseTransaction(true);
		try {
			DbService dbService = new DbService(trans);
			dbService.addChannelLog(channel.getId(), channel.getName(), "会话关闭");
			dbService.updateChannelStatus(channel.getId(), 2);
			trans.commit();
		} catch (Exception ex) {
			ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
			trans.rollback();
		} finally {
			trans.close();
		}
				
		//关闭线程
		if(null != heartbeatThread){
			heartbeatThread.stop();
		}
		if(null != submitThread){
			submitThread.stop_();
		}
		if(null != submitResponseThread){
			submitResponseThread.stop_();
		}
		if(null != deliverThread){
			deliverThread.stop_();
		}

		
		//清除缓存
		ChannelCacheUtil.clear("thread_" + channel.getId());
		ChannelCacheUtil.clear("message_" + channel.getId());
		
		ChannelLog.log(logger, session.getId() + "> Session closed", LevelUtils.getSucLevel(channel.getId()));
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		CmppMSG msg = (CmppMSG) message;
		
		switch (msg.header.getCommandId()) {
		case CmppConstant.CMD_CONNECT_RESP:
			ConnectResp conrsp = (ConnectResp) msg;
			
			if (conrsp.getStatus() == 0) {
				session.setAttribute("isSend", true);//设置可发送标识
				
				//更改状态为运行
				DatabaseTransaction trans = new DatabaseTransaction(true);
				try {
					DbService dbService = new DbService(trans);
					dbService.addChannelLog(channel.getId(), channel.getName(), "启动成功");
					dbService.updateChannelStatus(channel.getId(), 1);
					trans.commit();
				} catch (Exception ex) {
					ChannelLog.log(logger, ex.getMessage(), LevelUtils.getErrLevel(channel.getId()), ex.getCause());
					trans.rollback();
				} finally {
					trans.close();
				}
				
				channel.setStatus(Channel.RUN_STATUS);
				channel.setReConnect(true);
				
				//启动消息发送处理类
				submitThread = new SubmitThread(channel);
				submitThread.start();
				//将启动的线程加入缓存管理
				ChannelCacheUtil.put("thread_" + channel.getId(), "submitThread", submitThread);
				
				//启动发送响应处理类
				submitResponseThread = new SubmitResponseThread(channel);
				submitResponseThread.start();
				//将响应处理线程加入缓存管理
				ChannelCacheUtil.put("thread_" + channel.getId(), "submitResponseThread", submitResponseThread);
				
				//启动状态报告或者MT消息处理类
				deliverThread = new DeliverThread(channel);
				deliverThread.start();
				//将状态报告处理线程加入缓存管理
				ChannelCacheUtil.put("thread_" + channel.getId(), "deliverThread", deliverThread);
			} else {
				session.close(true);
			}
			break;
		case CmppConstant.CMD_ACTIVE_TEST_RESP:
			heartbeatThread.setLastActiveTime(System.currentTimeMillis());//更改最后心跳时间等于当前时间
			break;
		case CmppConstant.CMD_ACTIVE_TEST:
			ActiveTest activeTest = (ActiveTest) msg;
			ActiveTestResp activeTestResp = (ActiveTestResp) activeTest.getResponse();
			session.write(activeTestResp);
			break;
			
		case CmppConstant.CMD_TERMINATE:
			Terminate terminate = (Terminate) msg;
			ChannelLog.log(logger, "server requests to close the channel;", LevelUtils.getSucLevel(channel.getId()));
			
			TerminateResp terminateResp = (TerminateResp)terminate.getResponse();
			session.write(terminateResp);
			break;
		case CmppConstant.CMD_TERMINATE_RESP:
			session.close(true);
			break;
		case CmppConstant.CMD_SUBMIT_RESP:
			SubmitResp subresponse = (SubmitResp) msg;
			
			submitResponseThread.queue.offer(subresponse);//将数据加入处理队列
			
			break;
		case CmppConstant.CMD_DELIVER:
			Deliver cmppDeliver = (Deliver) msg;
			
			DeliverResp cmppDeliverResp = (DeliverResp) cmppDeliver .getResponse();
			cmppDeliverResp.setMsgId(cmppDeliver.getMsgId());
			session.write(cmppDeliverResp);
			
			deliverThread.queue.offer(cmppDeliver);//将数据加入处理队列

			break;
		default:
			ChannelLog.log(logger, "Unexpected MSG received! MSG Header: " + msg.header.getData().getHexDump(), LevelUtils.getErrLevel(channel.getId()));
			
			session.close(true);
			break;
		}
	}
}