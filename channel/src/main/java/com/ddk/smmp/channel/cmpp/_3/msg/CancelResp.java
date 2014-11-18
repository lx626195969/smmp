package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:25:40 li_mr_ceo@163.com <br>
 * 
 */
public class CancelResp extends Response {

	/**
	 * 成功标识。 0：成功； 1：失败。
	 */
	private int successId = 0;

	public CancelResp() {
		super(CmppConstant.CMD_CANCEL_RESP);
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(successId);
		return buffer;
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			successId = buffer.removeInt();
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	@Override
	public String name() {
		return "CMPP CancelResp";
	}
}