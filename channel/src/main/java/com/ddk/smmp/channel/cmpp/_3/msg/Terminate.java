package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Request;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午05:34:08 li_mr_ceo@163.com <br>
 *         断开连接消息 无消息体
 */
public class Terminate extends Request {

	public Terminate() {
		super(CmppConstant.CMD_TERMINATE);
	}

	@Override
	protected Response createResponse() {
		return new TerminateResp();
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
	}

	@Override
	public ByteBuffer getData() {
		return header.getData();
	}

	@Override
	public String name() {
		return "CMPP Terminate";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nTerminate***************************************"
				  + "\r\nshutdown package"
				  + "\r\n****************************************Terminate";
		return rt;
	}
}