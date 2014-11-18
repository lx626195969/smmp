package com.ddk.smmp.channel.smgp.msg;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:31:12 li_mr_ceo@163.com <br>
 *         本操作仅适用于通信双方采用长连接通信方式时用于保持连接
 */
public class ActiveTestResp extends Response {

	private byte reserve = 0x00;

	public ActiveTestResp() {
		super(SmgpConstant.CMD_ACTIVE_TEST_RESP);
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
	}

	@Override
	public ByteBuffer getData() {
		return header.getData();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			reserve = buffer.removeByte();
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendByte(reserve);
		return buffer;
	}

	@Override
	public String name() {
		return "SMGP ActiveTestResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nActiveTestResp***************************************"
				  + "\r\nheartbeat response package"
				  + "\r\n****************************************ActiveTestResp";
		return rt;
	}
}