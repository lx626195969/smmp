package com.ddk.smmp.channel.sgip.helper;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.msg.Bind;
import com.ddk.smmp.channel.sgip.msg.BindResp;
import com.ddk.smmp.channel.sgip.msg.Deliver;
import com.ddk.smmp.channel.sgip.msg.DeliverResp;
import com.ddk.smmp.channel.sgip.msg.Report;
import com.ddk.smmp.channel.sgip.msg.ReportResp;
import com.ddk.smmp.channel.sgip.msg.Submit;
import com.ddk.smmp.channel.sgip.msg.SubmitResp;
import com.ddk.smmp.channel.sgip.msg.Unbind;
import com.ddk.smmp.channel.sgip.msg.UnbindResp;
import com.ddk.smmp.channel.sgip.msg.header.SgipMSGHeader;
import com.ddk.smmp.channel.sgip.msg.parent.SgipMSG;
import com.ddk.smmp.channel.sgip.msg.parent.SmsObject;

/**
 * 
 * @author leeson 2014-6-10 上午11:01:21 li_mr_ceo@163.com <br>
 * 
 */
public class SgipMSGParser extends SmsObject {

	/**
	 * 通过BUFFER创建CmppMSG消息实体
	 * 
	 * @param buffer
	 * @return
	 */
	public static SgipMSG createMSGFromBuffer(ByteBuffer buffer) {
		SgipMSG msg = null;
		SgipMSGHeader msgHeader = new SgipMSGHeader();
		try {
			msgHeader.setData(buffer);//解析消息header
			//根据commandId解析成对应消息体
			switch (msgHeader.getCommandId()) {
			case SgipConstant.CMD_BIND:
				Bind bind = new Bind();
				bind.header = msgHeader;
				bind.setBody(buffer);
				msg = bind;
				break;
			case SgipConstant.CMD_BIND_RESP:
				BindResp bindResp = new BindResp();
				bindResp.header = msgHeader;
				bindResp.setBody(buffer);
				msg = bindResp;
				break;
			case SgipConstant.CMD_UNBIND:
				Unbind unbind = new Unbind();
				unbind.header = msgHeader;
				msg = unbind;
				break;
			case SgipConstant.CMD_UNBIND_RESP:
				UnbindResp unbindResp = new UnbindResp();
				unbindResp.header = msgHeader;
				msg = unbindResp;
				break;
			case SgipConstant.CMD_SUBMIT:
				Submit submit = new Submit();
				submit.header = msgHeader;
				submit.setBody(buffer);
				msg = submit;
				break;
			case SgipConstant.CMD_SUBMIT_RESP:
				SubmitResp submitResp = new SubmitResp();
				submitResp.header = msgHeader;
				submitResp.setBody(buffer);
				msg = submitResp;
				break;
			case SgipConstant.CMD_REPORT:
				Report report = new Report();
				report.header = msgHeader;
				report.setBody(buffer);
				msg = report;
				break;
			case SgipConstant.CMD_REPORT_RESP:
				ReportResp reportResp = new ReportResp();
				reportResp.header = msgHeader;
				reportResp.setBody(buffer);
				msg = reportResp;
				break;
			case SgipConstant.CMD_DELIVER:
				Deliver deliver = new Deliver();
				deliver.header = msgHeader;
				deliver.setBody(buffer);
				msg = deliver;
				break;
			case SgipConstant.CMD_DELIVER_RESP:
				DeliverResp deliverResp = new DeliverResp();
				deliverResp.header = msgHeader;
				deliverResp.setBody(buffer);
				msg = deliverResp;
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