package com.ddk.smmp.channel.sgip.msg;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午05:34:26 li_mr_ceo@163.com <br>
 * 
 */
public class UnbindResp extends Response {
	public UnbindResp() {
		super(SgipConstant.CMD_UNBIND_RESP);
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
		return "SGIP UnBindResp";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nUnBindResp***************************************"
				  + "\r\nshutdown response package"
				  + "\r\n****************************************UnBindResp";
		return rt;
	}
}