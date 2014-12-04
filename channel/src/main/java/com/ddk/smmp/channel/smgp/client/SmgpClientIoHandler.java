package com.ddk.smmp.channel.smgp.client;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.ConstantUtils;
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
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

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
		Login request = new Login();
		request.setClientId(channel.getAccount());
		request.setSharedSecret(channel.getPassword());
		request.setTimeStamp(request.genTimeStamp());
		request.setAuthClient(request.genAuthClient());
		request.assignSequenceNumber();
		session.write(request);
		
		// 启动ActiveTest链路心跳检测Thread
		((SmgpClient)(channel.getClient())).heartbeatThread = new ActiveTestThread(session, channel.getId());
		((SmgpClient)(channel.getClient())).heartbeatThread.start();
		
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
		if(null != ((SmgpClient)(channel.getClient())).heartbeatThread){
			((SmgpClient)(channel.getClient())).heartbeatThread.stop();
			((SmgpClient)(channel.getClient())).heartbeatThread = null;
			ChannelLog.log(logger, "停止心跳处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != ((SmgpClient)(channel.getClient())).submitThread){
			((SmgpClient)(channel.getClient())).submitThread.stop_();
			((SmgpClient)(channel.getClient())).submitThread = null;
			ChannelLog.log(logger, "停止短信提交处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != ((SmgpClient)(channel.getClient())).submitResponseThread){
			((SmgpClient)(channel.getClient())).submitResponseThread.stop_();
			((SmgpClient)(channel.getClient())).submitResponseThread = null;
			ChannelLog.log(logger, "停止短信提交响应处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != ((SmgpClient)(channel.getClient())).deliverThread){
			((SmgpClient)(channel.getClient())).deliverThread.stop_();
			((SmgpClient)(channel.getClient())).deliverThread = null;
			ChannelLog.log(logger, "停止短信上行和报告处理线程......", LevelUtils.getSucLevel(channel.getId()));
		}
		
		if(null != channel.getClient().connector){
			channel.getClient().connector.dispose();
		}
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
				ConstantUtils.updateChannelStatus(channel.getId(), 1);
				
				channel.setStatus(Channel.RUN_STATUS);
				
				//启动消息发送处理类
				((SmgpClient)(channel.getClient())).submitThread = new SubmitThread(channel);
				((SmgpClient)(channel.getClient())).submitThread.start();
				
				//启动发送响应处理类
				((SmgpClient)(channel.getClient())).submitResponseThread = new SubmitResponseThread(channel);
				((SmgpClient)(channel.getClient())).submitResponseThread.start();
				
				//启动状态报告或者MT消息处理类
				((SmgpClient)(channel.getClient())).deliverThread = new DeliverThread(channel);
				((SmgpClient)(channel.getClient())).deliverThread.start();
			} else {
				session.close(true);
			}
			break;
		case SmgpConstant.CMD_ACTIVE_TEST_RESP:
			((SmgpClient)(channel.getClient())).heartbeatThread.setLastActiveTime(System.currentTimeMillis());//更改最后心跳时间等于当前时间
			break;
		case SmgpConstant.CMD_ACTIVE_TEST:
			ActiveTest activeTest = (ActiveTest) msg;
			ActiveTestResp activeTestResp = (ActiveTestResp) activeTest.getResponse();
			session.write(activeTestResp);
			break;
			
		case SmgpConstant.CMD_EXIT:
			Exit terminate = (Exit) msg;
			ChannelLog.log(logger, "server requests to close the channel;", LevelUtils.getSucLevel(channel.getId()));
			ExitResp terminateResp = (ExitResp)terminate.getResponse();
			session.write(terminateResp);
			break;
		case SmgpConstant.CMD_EXIT_RESP:
			session.close(true);
			break;
		case SmgpConstant.CMD_SUBMIT_RESP:
			SubmitResp subresponse = (SubmitResp) msg;
			
			((SmgpClient)(channel.getClient())).submitResponseThread.queue.offer(subresponse);//将数据加入处理队列
			
			break;
		case SmgpConstant.CMD_DELIVER:
			Deliver cmppDeliver = (Deliver) msg;
			
			DeliverResp cmppDeliverResp = (DeliverResp) cmppDeliver .getResponse();
			cmppDeliverResp.setMsgId(cmppDeliver.getMsgId());
			session.write(cmppDeliverResp);
			
			((SmgpClient)(channel.getClient())).deliverThread.queue.offer(cmppDeliver);//将数据加入处理队列

			break;
		default:
			ChannelLog.log(logger, "Unexpected MSG received! MSG Header: " + msg.header.getData().getHexDump(), LevelUtils.getErrLevel(channel.getId()));
			session.close(true);
			break;
		}
	}
}