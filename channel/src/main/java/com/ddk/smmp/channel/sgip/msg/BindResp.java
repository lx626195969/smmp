package com.ddk.smmp.channel.sgip.msg;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月26日 下午12:18:41 li_mr_ceo@163.com <br>
 *
 */
public class BindResp extends Response {
	private int result = 0x00;
	private String reserve = "";

	public BindResp() {
		super(SgipConstant.CMD_BIND_RESP);
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setResult(buffer.removeByte());
			setReserve(buffer.removeStringEx(8));
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendByte((byte)getResult());
		buffer.appendString(getReserve(), 8);
		
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
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
	public String name() {
		return "SGIP BindResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nBindResp******************************************"
				  + "\r\nresult:        " + result
				  + "\r\nreserve:       " + reserve
				  + "\r\n******************************************BindResp";
		return rt;
	}
}
