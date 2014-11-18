package com.ddk.smmp.channel.cmpp._3.helper;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.msg.ActiveTest;
import com.ddk.smmp.channel.cmpp._3.msg.ActiveTestResp;
import com.ddk.smmp.channel.cmpp._3.msg.Cancel;
import com.ddk.smmp.channel.cmpp._3.msg.CancelResp;
import com.ddk.smmp.channel.cmpp._3.msg.Connect;
import com.ddk.smmp.channel.cmpp._3.msg.ConnectResp;
import com.ddk.smmp.channel.cmpp._3.msg.Deliver;
import com.ddk.smmp.channel.cmpp._3.msg.DeliverResp;
import com.ddk.smmp.channel.cmpp._3.msg.Query;
import com.ddk.smmp.channel.cmpp._3.msg.QueryResp;
import com.ddk.smmp.channel.cmpp._3.msg.Submit;
import com.ddk.smmp.channel.cmpp._3.msg.SubmitResp;
import com.ddk.smmp.channel.cmpp._3.msg.Terminate;
import com.ddk.smmp.channel.cmpp._3.msg.TerminateResp;
import com.ddk.smmp.channel.cmpp._3.msg.header.CmppMSGHeader;
import com.ddk.smmp.channel.cmpp._3.msg.parent.CmppMSG;
import com.ddk.smmp.channel.cmpp._3.msg.parent.SmsObject;

/**
 * 
 * @author leeson 2014-6-10 上午11:01:21 li_mr_ceo@163.com <br>
 * 
 */
public class CmppMSGParser extends SmsObject {

	/**
	 * 通过BUFFER创建CmppMSG消息实体
	 * 
	 * @param buffer
	 * @return
	 */
	public static CmppMSG createMSGFromBuffer(ByteBuffer buffer) {
		CmppMSG msg = null;
		CmppMSGHeader msgHeader = new CmppMSGHeader();
		try {
			msgHeader.setData(buffer);//解析消息header
			//根据commandId解析成对应消息体
			switch (msgHeader.getCommandId()) {
			case CmppConstant.CMD_CONNECT:
				Connect login = new Connect();
				login.header = msgHeader;
				login.setBody(buffer);
				msg = login;
				break;
			case CmppConstant.CMD_CONNECT_RESP:
				ConnectResp loginResp = new ConnectResp();
				loginResp.header = msgHeader;
				loginResp.setBody(buffer);
				msg = loginResp;
				break;
			case CmppConstant.CMD_TERMINATE:
				Terminate terminate = new Terminate();
				terminate.header = msgHeader;
				msg = terminate;
				break;
			case CmppConstant.CMD_TERMINATE_RESP:
				TerminateResp terminateResp = new TerminateResp();
				terminateResp.header = msgHeader;
				msg = terminateResp;
				break;
			case CmppConstant.CMD_SUBMIT:
				Submit submit = new Submit();
				submit.header = msgHeader;
				submit.setBody(buffer);
				msg = submit;
				break;
			case CmppConstant.CMD_SUBMIT_RESP:
				SubmitResp submitResp = new SubmitResp();
				submitResp.header = msgHeader;
				submitResp.setBody(buffer);
				msg = submitResp;
				break;
			case CmppConstant.CMD_QUERY:
				Query query = new Query();
				query.header = msgHeader;
				query.setBody(buffer);
				msg = query;
				break;
			case CmppConstant.CMD_QUERY_RESP:
				QueryResp queryResp = new QueryResp();
				queryResp.header = msgHeader;
				queryResp.setBody(buffer);
				msg = queryResp;
				break;
			case CmppConstant.CMD_DELIVER:
				Deliver deliver = new Deliver();
				deliver.header = msgHeader;
				deliver.setBody(buffer);
				msg = deliver;
				break;
			case CmppConstant.CMD_DELIVER_RESP:
				DeliverResp deliverResp = new DeliverResp();
				deliverResp.header = msgHeader;
				deliverResp.setBody(buffer);
				msg = deliverResp;
				break;
			case CmppConstant.CMD_CANCEL:
				Cancel cancel = new Cancel();
				cancel.header = msgHeader;
				cancel.setBody(buffer);
				msg = cancel;
				break;
			case CmppConstant.CMD_CANCEL_RESP:
				CancelResp cancelResp = new CancelResp();
				cancelResp.header = msgHeader;
				cancelResp.setBody(buffer);
				msg = cancelResp;
				break;
			case CmppConstant.CMD_ACTIVE_TEST:
				ActiveTest activeTest = new ActiveTest();
				activeTest.header = msgHeader;
				msg = activeTest;
				break;
			case CmppConstant.CMD_ACTIVE_TEST_RESP:
				ActiveTestResp activeTestResp = new ActiveTestResp();
				activeTestResp.header = msgHeader;
				msg = activeTestResp;
				break;
			default:
				logger.info("Unknown Command! MSG Header: " + msgHeader.getData().getHexDump());
				break;
			}
		} catch (MSGException e) {
			logger.info("Error parsing MSG: ", e);
		}
		return msg;
	}
}