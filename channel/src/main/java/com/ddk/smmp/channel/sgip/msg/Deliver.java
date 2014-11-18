package com.ddk.smmp.channel.sgip.msg;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.helper.ShortMessage;
import com.ddk.smmp.channel.sgip.msg.parent.Request;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:16:32 li_mr_ceo@163.com <br>
 * 
 */
public class Deliver extends Request {
	private String userNumber = "";
	private String spNumber = "";
	private byte tpPid = 0;
	private byte tpUdhi = 0;
	private ShortMessage sm = new ShortMessage();
	private String reserve = "";

	public Deliver() {
		super(SgipConstant.CMD_DELIVER);
	}

	@Override
	protected Response createResponse() {
		return new DeliverResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			userNumber = buffer.removeStringEx(21);
			spNumber = buffer.removeStringEx(21);
			tpPid = buffer.removeByte();
			tpUdhi = buffer.removeByte();
			byte msgFormat = buffer.removeByte();
			int msgLength = buffer.removeInt();
			sm.setData(buffer.removeBuffer(msgLength));
			sm.setMsgFormat(msgFormat);
			if(tpUdhi == 1){
				sm.setSuper(true);
			}
			reserve = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(userNumber, 21);
		buffer.appendString(spNumber, 21);
		buffer.appendByte(tpPid);
		buffer.appendByte(tpUdhi);
		buffer.appendByte(sm.getMsgFormat());
		buffer.appendInt(sm.getLength());
		buffer.appendBuffer(sm.getData());
		buffer.appendString(reserve, 8);
		return buffer;
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

	public byte getTpPid() {
		return tpPid;
	}

	public void setTpPid(byte tpPid) {
		this.tpPid = tpPid;
	}

	public byte getTpUdhi() {
		return tpUdhi;
	}

	public void setTpUdhi(byte tpUdhi) {
		this.tpUdhi = tpUdhi;
	}

	public ShortMessage getSm() {
		return sm;
	}

	public void setSm(ShortMessage sm) {
		this.sm = sm;
	}
	
	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	@Override
	public String dump() {
		String rt = "\r\nDeliver***************************************"
			      + "\r\nuserNumber:        " + userNumber
			      + "\r\nspNumber:          " + spNumber
			      + "\r\ntpPid:             " + tpPid
			      + "\r\ntpUdhi:            " + tpUdhi
			      + "\r\nmsgFormat:         " + sm.getMsgFormat()
			      + "\r\nmsgLength:         " + sm.getLength()
			      + "\r\nmsgContent:        " + sm.getMessage()
			      + "\r\nreserve:           " + reserve
			      + "\r\n****************************************Deliver";
		return rt;
	}

	@Override
	public String name() {
		return "SGIP Deliver";
	}
}