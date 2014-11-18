package com.ddk.smmp.channel.smgp.msg.header;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.ByteData;

/**
 * 
 * @author leeson 2014-6-9 下午02:21:37 li_mr_ceo@163.com
 * 
 */
public class SmgpMSGHeader extends ByteData {
	private int commandLength = SmgpConstant.PDU_HEADER_SIZE;
	private int commandId = 0;
	private int sequenceNumber = 0;

	@Override
	public ByteBuffer getData() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(getCommandLength());
		buffer.appendInt(getCommandId());
		buffer.appendInt(getSequenceNumber());
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		try {
			commandLength = buffer.removeInt();
			commandId = buffer.removeInt();
			sequenceNumber = buffer.removeInt();
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public int getCommandLength() {
		return commandLength;
	}

	public void setCommandLength(int cmdLen) {
		commandLength = cmdLen;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int cmdId) {
		commandId = cmdId;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int seqNr) {
		sequenceNumber = seqNr;
	}
}