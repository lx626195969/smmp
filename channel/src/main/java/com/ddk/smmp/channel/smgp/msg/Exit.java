package com.ddk.smmp.channel.smgp.msg;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Request;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午05:34:08 li_mr_ceo@163.com <br>
 *         断开连接消息 无消息体
 */
public class Exit extends Request {

	public Exit() {
		super(SmgpConstant.CMD_EXIT);
	}

	@Override
	protected Response createResponse() {
		return new ExitResp();
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
		return "SMGP Exit";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nExit***************************************"
				  + "\r\nshutdown package"
				  + "\r\n****************************************Exit";
		return rt;
	}
}