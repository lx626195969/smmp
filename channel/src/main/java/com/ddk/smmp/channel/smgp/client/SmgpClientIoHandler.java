package com.ddk.smmp.channel.smgp.client;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ChannelCacheUtil;
import com.ddk.smmp.channel.smgp.handler.ActiveTestThread;
import com.ddk.smmp.channel.smgp.handler.DeliverThread;
import com.ddk.smmp.channel.smgp.handler.SubmitResponseThread;
import com.ddk.smmp.channel.smgp.handler.SubmitThread;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.ActiveTest;
import com.ddk.smmp.channel.smgp.msg.ActiveTestResp;
import com.ddk.smmp.channel.smgp.msg.Deliver;
import com.ddk.smmp.channel.smgp.msg.DeliverResp;
import com.ddk.smmp.channel.smgp.msg.Exit;
import com.ddk.smmp.channel.smgp.msg.ExitResp;
import com.ddk.smmp.channel.smgp.msg.Login;
import com.ddk.smmp.channel.smgp.msg.LoginResp;
import com.ddk.smmp.channel.smgp.msg.SubmitResp;
import com.ddk.smmp.channel.smgp.msg.parent.SmgpMSG;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014-6-10 上午11:22:44 li_mr_ceo@163.com <br>
 *         客户端消息处理类
 */
public class SmgpClientIoHandler extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(SmgpClientIoHandler.class);
	
	private Channel channel = null;
	public SmgpClientIoHandler(Channel channel) {
		this.channel = channel;
	}
	
	SubmitThread submitThread = null;
	SubmitResponseThread submitResponseThread = null;
	ActiveTestThread heartbeatThread = null;
	DeliverThread deliverThread = null;
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		if (!(cause instanceof IOException)) {
			logger.info("Exception: ", cause);
		} else {
			logger.info("I/O error: " + cause.getMessage());
		}
		session.close(true);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("Session " + session.getId() + " is opened");
		
		channel.setSession(session);
		
		// 向服务端发起Connect消息
		Login request = new Login(SmgpConstant.TRANSMITTER);
		request.setClientId(channel.getCompanyCode());
		request.setSharedSecret(channel.getPassword());
		request.setTimeStamp(request.genTimeStamp());
		request.setAuthClient(request.genAuthClient());
		request.assignSequenceNumber();
		session.write(request);
		
		// 启动ActiveTest链路心跳检测Thread
		heartbeatThread = new ActiveTestThread(session);
		heartbeatThread.start();
		//将启动的线程加入缓存管理
		ChannelCacheUtil.put("thread_" + channel.getId(), "heartbeatThread", heartbeatThread);
		
		session.resumeRead();//恢复读取
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("Creation of session " + session.getId());
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
		
		logger.info(session.getId() + "> Session closed");
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		SmgpMSG msg = (SmgpMSG) message;
		
		switch (msg.header.getCommandId()) {
		case SmgpConstant.CMD_LOGIN_RESP:
			LoginResp conrsp = (LoginResp) msg;
			
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
		case SmgpConstant.CMD_ACTIVE_TEST_RESP:
			heartbeatThread.setLastActiveTime(System.currentTimeMillis());//更改最后心跳时间等于当前时间
			break;
		case SmgpConstant.CMD_ACTIVE_TEST:
			ActiveTest activeTest = (ActiveTest) msg;
			ActiveTestResp activeTestResp = (ActiveTestResp) activeTest.getResponse();
			session.write(activeTestResp);
			break;
			
		case SmgpConstant.CMD_EXIT:
			Exit terminate = (Exit) msg;
			logger.info("server requests to close the channel;");
			ExitResp terminateResp = (ExitResp)terminate.getResponse();
			session.write(terminateResp);
			break;
		case SmgpConstant.CMD_EXIT_RESP:
			session.close(true);
			break;
		case SmgpConstant.CMD_SUBMIT_RESP:
			SubmitResp subresponse = (SubmitResp) msg;
			
			submitResponseThread.queue.offer(subresponse);//将数据加入处理队列
			
			break;
		case SmgpConstant.CMD_DELIVER:
			Deliver cmppDeliver = (Deliver) msg;
			
			DeliverResp cmppDeliverResp = (DeliverResp) cmppDeliver .getResponse();
			cmppDeliverResp.setMsgId(cmppDeliver.getMsgId());
			session.write(cmppDeliverResp);
			
			deliverThread.queue.offer(cmppDeliver);//将数据加入处理队列

			break;
		default:
			logger.warn("Unexpected MSG received! MSG Header: " + msg.header.getData().getHexDump());
			session.close(true);
			break;
		}
	}
}