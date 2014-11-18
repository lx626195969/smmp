package com.ddk.smmp.channel.sgip.msg;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月26日 下午2:21:18 li_mr_ceo@163.com <br>
 *
 */
public class SubmitResp extends Response {
	private int result = 0;
	private String reserve = "";
	
	public SubmitResp() {
		super(SgipConstant.CMD_SUBMIT_RESP);
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(SgipConstant.PDU_HEADER_SIZE + bodyBuf.length());
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
			result = buffer.removeByte();
			reserve = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendByte((byte)result);
		buffer.appendString(reserve, 8);
		return buffer;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String name() {
		return "SGIP SubmitResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nSubmitResp********************************"
				  + "\r\nresult:     " + result
				  + "\r\nreserve:    " + reserve
				  + "\r\n********************************SubmitResp";
		return rt;
	}
}