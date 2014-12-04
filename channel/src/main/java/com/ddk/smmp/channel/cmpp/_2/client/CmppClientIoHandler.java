package com.ddk.smmp.channel.cmpp._2.client;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ConstantUtils;
import com.ddk.smmp.channel.cmpp._2.handler.ActiveTestThread;
import com.ddk.smmp.channel.cmpp._2.handler.DeliverThread;
import com.ddk.smmp.channel.cmpp._2.handler.SubmitResponseThread;
import com.ddk.smmp.channel.cmpp._2.handler.SubmitThread;
import com.ddk.smmp.channel.cmpp._2.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._2.msg.ActiveTest;
import com.ddk.smmp.channel.cmpp._2.msg.ActiveTestResp;
import com.ddk.smmp.channel.cmpp._2.msg.Connect;
import com.ddk.smmp.channel.cmpp._2.msg.ConnectResp;
import com.ddk.smmp.channel.cmpp._2.msg.Deliver;
import com.ddk.smmp.channel.cmpp._2.msg.DeliverResp;
import com.ddk.smmp.channel.cmpp._2.msg.SubmitResp;
import com.ddk.smmp.channel.cmpp._2.msg.Terminate;
import com.ddk.smmp.channel.cmpp._2.msg.TerminateResp;
import com.ddk.smmp.channel.cmpp._2.msg.parent.CmppMSG;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

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
		request.setVersion(32);
		request.assignSequenceNumber();
		session.write(request);
		
		// 启动ActiveTest链路心跳检测Thread
		((Cmpp2_0Client)(channel.getClient())).heartbeatThread = new ActiveTestThread(session, channel.getId());
		((Cmpp2_0Client)(channel.getClient())).heartbeatThread.start();
		
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
		ChannelLog.log(logger, session.getId() + "> Session closed", LevelUtils.getSucLevel(channel.getId()));
		session.removeAttribute("isSend");//移除可发送标识
				
		//关闭线程
		if(null != ((Cmpp2_0Client)(channel.getClient())).heartbeatThread){
			((Cmpp2_0Client)(channel.getClient())).heartbeatThread.stop();
			((Cmpp2_0Client)(channel.getClient())).heartbeatThread = null;
			ChannelLog.log(logger, "停止心跳处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != ((Cmpp2_0Client)(channel.getClient())).submitThread){
			((Cmpp2_0Client)(channel.getClient())).submitThread.stop_();
			((Cmpp2_0Client)(channel.getClient())).submitThread = null;
			ChannelLog.log(logger, "停止短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != ((Cmpp2_0Client)(channel.getClient())).submitResponseThread){
			((Cmpp2_0Client)(channel.getClient())).submitResponseThread.stop_();
			((Cmpp2_0Client)(channel.getClient())).submitResponseThread = null;
			ChannelLog.log(logger, "停止短信提交响应处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != ((Cmpp2_0Client)(channel.getClient())).deliverThread){
			((Cmpp2_0Client)(channel.getClient())).deliverThread.stop_();
			((Cmpp2_0Client)(channel.getClient())).deliverThread = null;
			ChannelLog.log(logger, "停止短信上行和报告处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != channel.getClient().connector){
			channel.getClient().connector.dispose();
		}
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		CmppMSG msg = (CmppMSG) message;
		
		switch (msg.header.getCommandId()) {
		case CmppConstant.CMD_CONNECT_RESP:
			ConnectResp conrsp = (ConnectResp) msg;
			
			if (conrsp.getStatus() == 0) {
				//设置可发送标识
				session.setAttribute("isSend", true);
				
				//更改状态为运行
				ConstantUtils.updateChannelStatus(channel.getId(), 1);
				
				channel.setStatus(Channel.RUN_STATUS);
				
				//启动消息发送处理类
				((Cmpp2_0Client)(channel.getClient())).submitThread = new SubmitThread(channel);
				((Cmpp2_0Client)(channel.getClient())).submitThread.start();
				
				//启动发送响应处理类
				((Cmpp2_0Client)(channel.getClient())).submitResponseThread = new SubmitResponseThread(channel);
				((Cmpp2_0Client)(channel.getClient())).submitResponseThread.start();
				
				//启动状态报告或者MT消息处理类
				((Cmpp2_0Client)(channel.getClient())).deliverThread = new DeliverThread(channel);
				((Cmpp2_0Client)(channel.getClient())).deliverThread.start();
			} else {
				session.close(true);
			}
			break;
		case CmppConstant.CMD_ACTIVE_TEST_RESP:
			//更改最后心跳时间等于当前时间
			((Cmpp2_0Client)(channel.getClient())).heartbeatThread.setLastActiveTime(System.currentTimeMillis());
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
			SubmitResp subresp = (SubmitResp) msg;
			
			//将数据加入处理队列
			((Cmpp2_0Client)(channel.getClient())).submitResponseThread.queue.offer(subresp);
			
			break;
		case CmppConstant.CMD_DELIVER:
			Deliver cmppDeliver = (Deliver) msg;
			
			DeliverResp cmppDeliverResp = (DeliverResp) cmppDeliver .getResponse();
			cmppDeliverResp.setMsgId(cmppDeliver.getMsgId());
			session.write(cmppDeliverResp);
			
			//将数据加入处理队列
			((Cmpp2_0Client)(channel.getClient())).deliverThread.queue.offer(cmppDeliver);

			break;
		default:
			ChannelLog.log(logger, "Unexpected MSG received! MSG Header: " + msg.header.getData().getHexDump(), LevelUtils.getErrLevel(channel.getId()));
			session.close(true);
			break;
		}
	}
}