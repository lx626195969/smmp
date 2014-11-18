package com.ddk.smmp.channel.sgip.msg.header;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.ByteData;

/**
 * 
 * @author leeson 2014年6月26日 上午11:17:10 li_mr_ceo@163.com <br>
 *
 */
public class SgipMSGHeader extends ByteData {
	private int commandLength = SgipConstant.PDU_HEADER_SIZE;
	private int commandId = 0;
	private int sequenceNumber1 = 0;
	private int sequenceNumber2 = 0;
	private int sequenceNumber3 = 0;

	@Override
	public ByteBuffer getData() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(getCommandLength());
		buffer.appendInt(getCommandId());
		buffer.appendInt(getSequenceNumber1());
		buffer.appendInt(getSequenceNumber2());
		buffer.appendInt(getSequenceNumber3());
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		try {
			commandLength = buffer.removeInt();
			commandId = buffer.removeInt();
			sequenceNumber1 = buffer.removeInt();
			sequenceNumber2 = buffer.removeInt();
			sequenceNumber3 = buffer.removeInt();
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

	/** NODEID */
	public int getSequenceNumber1() {
		return sequenceNumber1;
	}

	public void setSequenceNumber1(int sequenceNumber1) {
		this.sequenceNumber1 = sequenceNumber1;
	}

	/** MMddHHmmss */
	public int getSequenceNumber2() {
		return sequenceNumber2;
	}

	public void setSequenceNumber2(int sequenceNumber2) {
		this.sequenceNumber2 = sequenceNumber2;
	}

	/** SEQ */
	public int getSequenceNumber3() {
		return sequenceNumber3;
	}

	public void setSequenceNumber3(int sequenceNumber3) {
		this.sequenceNumber3 = sequenceNumber3;
	}
}