package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Request;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:32:26 li_mr_ceo@163.com <br>
 * 
 */
public class ActiveTest extends Request {

	public ActiveTest() {
		super(CmppConstant.CMD_ACTIVE_TEST);
	}

	@Override
	protected Response createResponse() {
		return new ActiveTestResp();
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
		return "CMPP ActiveTest";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nActiveTest***************************************"
				  + "\r\nheartbeat package"
				  + "\r\n****************************************ActiveTest";
		return rt;
	}
}