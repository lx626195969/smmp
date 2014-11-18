package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Request;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:27:08 li_mr_ceo@163.com <br>
 *         CMPP_CANCEL操作的目的是SP通过此操作可以将已经提交给ISMG的短信删除，ISMG将以CMPP_CANCEL_RESP回应删除操作的结果
 */
public class Cancel extends Request {

	/**
	 * 信息标识（SP想要删除的信息标识）
	 */
	private String msgId = "";

	public Cancel() {
		super(CmppConstant.CMD_CANCEL);
	}

	@Override
	protected Response createResponse() {
		return new CancelResp();
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header
				.setCommandLength(CmppConstant.PDU_HEADER_SIZE
						+ bodyBuf.length());
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
		buffer.appendString(msgId, 8);
		return buffer;
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	@Override
	public String name() {
		return "CMPP Cancel";
	}
}