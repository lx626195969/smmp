package com.ddk.smmp.channel.smgp.msg;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Response;
import com.ddk.smmp.channel.smgp.utils.Tools;

/**
 * 
 * @author leeson 2014年6月25日 上午11:53:33 li_mr_ceo@163.com <br>
 *
 */
public class SubmitResp extends Response {
	private String msgId = "";
	private int result = 0;

	public SubmitResp() {
		super(SmgpConstant.CMD_SUBMIT_RESP);
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(SmgpConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = Tools.resolveSMGP_MsgId(buffer.removeBytes(10).getBuffer());
			result = buffer.removeInt();
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(msgId, 10);
		buffer.appendInt(result);
		return buffer;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String name() {
		return "SMGP SubmitResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nSubmitResp********************************"
				  + "\r\nmsgId:      " + msgId
				  + "\r\nresult:     " + result
				  + "\r\n********************************SubmitResp";
		return rt;
	}
}