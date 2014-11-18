package com.ddk.smmp.channel.smgp.msg;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午05:34:26 li_mr_ceo@163.com <br>
 * 
 */
public class ExitResp extends Response {
	public ExitResp() {
		super(SmgpConstant.CMD_EXIT_RESP);
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
		return "SMGP ExitResp";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nExitResp***************************************"
				  + "\r\nshutdown response package"
				  + "\r\n****************************************ExitResp";
		return rt;
	}
}