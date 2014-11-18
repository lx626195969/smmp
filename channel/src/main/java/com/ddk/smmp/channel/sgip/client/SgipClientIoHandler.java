package com.ddk.smmp.channel.sgip.client;

import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.log4j.Logger;



import com.ddk.smmp.channel.Channel;
import com.ddk.smmp.channel.sgip.handler.SubmitResponseThread;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.Bind;
import com.ddk.smmp.channel.sgip.msg.BindResp;
import com.ddk.smmp.channel.sgip.msg.SubmitResp;
import com.ddk.smmp.channel.sgip.msg.Unbind;
import com.ddk.smmp.channel.sgip.msg.UnbindResp;
import com.ddk.smmp.channel.sgip.msg.parent.SgipMSG;
import com.ddk.smmp.jdbc.database.DatabaseTransaction;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;
import com.ddk.smmp.service.DbService;

/**
 * 
 * @author leeson 2014-6-10 上午11:22:44 li_mr_ceo@163.com <br>
 *         客户端消息处理类
 */
public class SgipClientIoHandler extends IoHandlerAdapter {
	private static final Logger logger = Logger.getLogger(SgipClientIoHandler.class);
	
	private Channel channel = null;
	SubmitResponseThread submitResponseThread;
	public SgipClientIoHandler(Channel channel, SubmitResponseThread submitResponseThread) {
		this.channel = channel;
		this.submitResponseThread = submitResponseThread;
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
		
		channel.setSession(session);
		
		// 向服务端发起Connect消息
		Bind request = new Bind();
		request.setLoginName(channel.getAccount());
		request.setLoginPwd(channel.getPassword());
		request.assignSequenceNumber(channel.getNodeId());
		session.write(request);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		ChannelLog.log(logger, "Creation of session " + session.getId(), LevelUtils.getSucLevel(channel.getId()));
		session.suspendRead();//暂停读取
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		ChannelLog.log(logger, session.getId() + "> Session closed", LevelUtils.getSucLevel(channel.getId()));
		
		session.removeAttribute("isSend");//移除可发送标识
		
		channel.setStatus(Channel.STOP_STATUS);
		
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
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		SgipMSG msg = (SgipMSG) message;
		
		switch (msg.header.getCommandId()) {
		case SgipConstant.CMD_BIND_RESP:
			BindResp bindResp = (BindResp) msg;
			
			if (bindResp.getResult() == 0) {
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
			} else {
				session.close(true);
			}
			break;
		case SgipConstant.CMD_UNBIND:
			Unbind unbind = (Unbind) msg;
			ChannelLog.log(logger, "server requests to close the channel;", LevelUtils.getSucLevel(channel.getId()));
			UnbindResp unbindResp = (UnbindResp)unbind.getResponse();
			session.write(unbindResp);
			break;
		case SgipConstant.CMD_UNBIND_RESP:
			session.close(true);
			break;
		case SgipConstant.CMD_SUBMIT_RESP:
			SubmitResp subresponse = (SubmitResp) msg;
			
			submitResponseThread.queue.offer(subresponse);//将数据加入处理队列
			
			break;
		default:
			ChannelLog.log(logger, "Unexpected MSG received! MSG Header: " + msg.header.getData().getHexDump(), LevelUtils.getErrLevel(channel.getId()));
			session.close(true);
			break;
		}
	}
}