package com.ddk.smmp.channel.sgip.msg;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.Request;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午05:34:08 li_mr_ceo@163.com <br>
 *         断开连接消息 无消息体
 */
public class Unbind extends Request {

	public Unbind() {
		super(SgipConstant.CMD_UNBIND);
	}

	@Override
	protected Response createResponse() {
		return new UnbindResp();
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
		return "SGIP UnBind";
	}
	
	@Override
	public String dump() {
		String rt = "\r\nUnBind***************************************"
				  + "\r\nshutdown package"
				  + "\r\n****************************************UnBind";
		return rt;
	}
}