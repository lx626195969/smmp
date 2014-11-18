package com.ddk.smmp.channel.cmpp._2.msg;

import com.ddk.smmp.channel.cmpp._2.exception.MSGException;
import com.ddk.smmp.channel.cmpp._2.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._2.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._2.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午05:34:26 li_mr_ceo@163.com <br>
 * 
 */
public class TerminateResp extends Response {
	public TerminateResp() {
		super(CmppConstant.CMD_TERMINATE_RESP);
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
		return "CMPP TerminateResp";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nTerminateResp***************************************"
				  + "\r\nshutdown response package"
				  + "\r\n****************************************TerminateResp";
		return rt;
	}
}