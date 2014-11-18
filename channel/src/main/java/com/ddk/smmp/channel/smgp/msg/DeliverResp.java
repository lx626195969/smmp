package com.ddk.smmp.channel.smgp.msg;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:12:11 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverResp extends Response {
	private String msgId = "";
	private int status = 0;

	public DeliverResp() {
		super(SmgpConstant.CMD_DELIVER_RESP);
	}

	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(SmgpConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = buffer.removeStringEx(10);
			status = buffer.removeInt();
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(msgId, 10);
		buffer.appendInt(status);
		return buffer;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String name() {
		return "SMGP DeliverResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nDeliverResp*****************************"
				  + "\r\nmsgId:     " + msgId
				  + "\r\nstatus:    " + status
				  + "\r\n*****************************DeliverResp";
		return rt;
	}
}