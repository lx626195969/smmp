package com.ddk.smmp.channel.smgp.helper;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.msg.ActiveTest;
import com.ddk.smmp.channel.smgp.msg.ActiveTestResp;
import com.ddk.smmp.channel.smgp.msg.Deliver;
import com.ddk.smmp.channel.smgp.msg.DeliverResp;
import com.ddk.smmp.channel.smgp.msg.Exit;
import com.ddk.smmp.channel.smgp.msg.ExitResp;
import com.ddk.smmp.channel.smgp.msg.Login;
import com.ddk.smmp.channel.smgp.msg.LoginResp;
import com.ddk.smmp.channel.smgp.msg.Submit;
import com.ddk.smmp.channel.smgp.msg.SubmitResp;
import com.ddk.smmp.channel.smgp.msg.header.SmgpMSGHeader;
import com.ddk.smmp.channel.smgp.msg.parent.SmgpMSG;
import com.ddk.smmp.channel.smgp.msg.parent.SmsObject;

/**
 * 
 * @author leeson 2014-6-10 上午11:01:21 li_mr_ceo@163.com <br>
 * 
 */
public class SmgpMSGParser extends SmsObject {

	/**
	 * 通过BUFFER创建SmgpMSG消息实体
	 * 
	 * @param buffer
	 * @return
	 */
	public static SmgpMSG createMSGFromBuffer(ByteBuffer buffer) {
		SmgpMSG msg = null;
		SmgpMSGHeader msgHeader = new SmgpMSGHeader();
		try {
			msgHeader.setData(buffer);//解析消息header
			//根据commandId解析成对应消息体
			switch (msgHeader.getCommandId()) {
			case SmgpConstant.CMD_LOGIN:
				Login login = new Login();
				login.header = msgHeader;
				login.setBody(buffer);
				msg = login;
				break;
			case SmgpConstant.CMD_LOGIN_RESP:
				LoginResp loginResp = new LoginResp();
				loginResp.header = msgHeader;
				loginResp.setBody(buffer);
				msg = loginResp;
				break;
			case SmgpConstant.CMD_EXIT:
				Exit terminate = new Exit();
				terminate.header = msgHeader;
				msg = terminate;
				break;
			case SmgpConstant.CMD_EXIT_RESP:
				ExitResp terminateResp = new ExitResp();
				terminateResp.header = msgHeader;
				msg = terminateResp;
				break;
			case SmgpConstant.CMD_SUBMIT:
				Submit submit = new Submit();
				submit.header = msgHeader;
				submit.setBody(buffer);
				msg = submit;
				break;
			case SmgpConstant.CMD_SUBMIT_RESP:
				SubmitResp submitResp = new SubmitResp();
				submitResp.header = msgHeader;
				submitResp.setBody(buffer);
				msg = submitResp;
				break;
			case SmgpConstant.CMD_DELIVER:
				Deliver deliver = new Deliver();
				deliver.header = msgHeader;
				deliver.setBody(buffer);
				msg = deliver;
				break;
			case SmgpConstant.CMD_DELIVER_RESP:
				DeliverResp deliverResp = new DeliverResp();
				deliverResp.header = msgHeader;
				deliverResp.setBody(buffer);
				msg = deliverResp;
				break;
			case SmgpConstant.CMD_ACTIVE_TEST:
				ActiveTest activeTest = new ActiveTest();
				activeTest.header = msgHeader;
				msg = activeTest;
				break;
			case SmgpConstant.CMD_ACTIVE_TEST_RESP:
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