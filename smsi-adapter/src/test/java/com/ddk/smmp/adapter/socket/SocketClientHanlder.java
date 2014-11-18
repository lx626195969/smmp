package com.ddk.smmp.adapter.socket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddk.smmp.adapter.socket.entity.BalanceRequest;
import com.ddk.smmp.adapter.socket.entity.BalanceResponse;
import com.ddk.smmp.adapter.socket.entity.ConnectRequest;
import com.ddk.smmp.adapter.socket.entity.ConnectResponse;
import com.ddk.smmp.adapter.socket.entity.DeliverResponse;
import com.ddk.smmp.adapter.socket.entity.ReportResponse;
import com.ddk.smmp.adapter.socket.entity.SubmitRequest;
import com.ddk.smmp.adapter.socket.entity.SubmitResponse;
import com.ddk.smmp.adapter.socket.entity.helper.Msg;
import com.ddk.smmp.adapter.utils.Constants;
import com.ddk.smmp.adapter.utils.SeqUtil;

/**
 * 
 * @author leeson 2014年7月8日 上午9:19:54 li_mr_ceo@163.com <br>
 * 
 */
public class SocketClientHanlder extends IoHandlerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(SocketClientHanlder.class);
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		super.sessionCreated(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		String userName = "leeson";
		String password = "qwe123!@#";
		
		ConnectRequest connectRequest = new ConnectRequest(userName, password);
		Msg msg = new Msg(Constants.SOCKET_COMMAND_CONNECT, SeqUtil.generateSeq(), connectRequest.toJson(null));
		
		session.write(msg.toJson());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		super.sessionIdle(session, status);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String key = "OfwcAPYjoH0DIsSdFP+DRw==";
		
		Msg msg = Msg.toObj(message.toString());
		
		if(null == msg){
			logger.info("###ILLEGAL REQUEST###" + message.toString() + "\r\n");
			session.close(true);
		}
		
		switch (msg.getCommandId()) {
		case Constants.SOCKET_COMMAND_CONNECT_RESP:
			ConnectResponse connectResponse = new ConnectResponse();
			connectResponse = connectResponse.toObj(msg.getBody(), null);
			
			logger.info("###RECEIVE###" + msg.toString() + "\r\n" + connectResponse.toString() + "\r\n");
			
			if(connectResponse.getCode() == Constants.CONNECT_OK){
				/*================提交短信===============*/
				String[] phones2 = new String[2000];
				for(int i = 10001000; i <= 10002999; i ++){
					phones2[i-10001000] = "152" + i;
				}
				String content = "大家下午好，今天下午6点在小会议室开会商讨上市方案，请提前安排好工作，准时参会。";
				Integer productId = 9;
				String sendTime = "";
				String expId = "";
				
				SubmitRequest request = new SubmitRequest(phones2, content, expId, productId, sendTime);
				Msg send1 = new Msg(Constants.SOCKET_COMMAND_SUBMIT, SeqUtil.generateSeq(), request.toJson(key));
				session.write(send1.toJson());
				
				/*================查询余额===============*/
				BalanceRequest balanceRequest = new BalanceRequest();
				Msg send2 = new Msg(Constants.SOCKET_COMMAND_BALANCE, SeqUtil.generateSeq(), balanceRequest.toJson(key));
				session.write(send2.toJson());
			}
			break;
		case Constants.SOCKET_COMMAND_SUBMIT_RESP:
			SubmitResponse submitResponse = new SubmitResponse();
			submitResponse = submitResponse.toObj(msg.getBody(), key);
			
			logger.info("###RECEIVE###" + msg.toString() + "\r\n" + submitResponse.toString() + "\r\n");
			break;
		case Constants.SOCKET_COMMAND_DELIVER_RESP:
			DeliverResponse deliverResponse = new DeliverResponse();
			deliverResponse = deliverResponse.toObj(msg.getBody(), key);
			
			logger.info("###RECEIVE###" + msg.toString() + "\r\n" + deliverResponse.toString() + "\r\n");
			break;
		case Constants.SOCKET_COMMAND_REPORT_RESP:
			ReportResponse reportResponse = new ReportResponse();
			reportResponse = reportResponse.toObj(msg.getBody(), key);
			
			logger.info("###RECEIVE###" + msg.toString() + "\r\n" + reportResponse.toString() + "\r\n");
			break;
		case Constants.SOCKET_COMMAND_BALANCE_RESP:
			BalanceResponse balanceResponse = new BalanceResponse();
			balanceResponse = balanceResponse.toObj(msg.getBody(), key);
			
			logger.info("###RECEIVE###" + msg.toString() + "\r\n" + balanceResponse.toString() + "\r\n");
			break;
		default:
			break;
		}
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		super.messageSent(session, message);
	}
}