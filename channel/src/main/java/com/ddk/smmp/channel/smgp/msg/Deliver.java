package com.ddk.smmp.channel.smgp.msg;

import java.util.Arrays;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.ShortMessage;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Request;
import com.ddk.smmp.channel.smgp.msg.parent.Response;
import com.ddk.smmp.channel.smgp.utils.Tools;

/**
 * 
 * @author leeson 2014-6-10 上午09:16:32 li_mr_ceo@163.com <br>
 * 
 */
public class Deliver extends Request {

	private String msgId = "";
	private byte isReport = 0;
	private ShortMessage sm = new ShortMessage();
	private String recvTime = "";
	private String srcTermId = "";
	private String dstTermId = "";
	private String reserve = "";
	
	public Deliver() {
		super(SmgpConstant.CMD_DELIVER);
	}

	@Override
	protected Response createResponse() {
		return new DeliverResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = Tools.resolveSMGP_MsgId(buffer.removeBytes(10).getBuffer());
			isReport = buffer.removeByte();
			byte msgFormat = buffer.removeByte();
			recvTime = buffer.removeStringEx(14);
			srcTermId = buffer.removeStringEx(21);
			dstTermId = buffer.removeStringEx(21);
			
			byte signbyte = buffer.removeByte();
			int msgLength = signbyte < 0 ? signbyte + 256 : signbyte;
			if (msgLength > 0)
				sm.setData(buffer.removeBuffer(msgLength));
			sm.setMsgFormat(msgFormat);
			
			byte[] temp = sm.getData().getBuffer();//判断是不是长短信
			if(temp[0] == 0x05 && temp[1] == 0x00 && temp[2] == 0x03){
				sm.setSuper(true);
			}
			
			reserve = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(msgId, 10);
		buffer.appendByte(isReport);
		buffer.appendByte(sm.getMsgFormat());
		buffer.appendString(recvTime, 14);
		buffer.appendString(srcTermId, 21);
		buffer.appendString(dstTermId, 21);
		buffer.appendByte((byte) sm.getLength());
		buffer.appendBuffer(sm.getData());
		buffer.appendString(reserve, 8);
		return buffer;
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

	public String getMsgContent() {
		return sm.getMessage();
	}

	public byte getMsgFormat() {
		return sm.getMsgFormat();
	}

	public byte getMsgLength() {
		return (byte) sm.getLength();
	}

	public ShortMessage getSm() {
		return sm;
	}

	public void setSm(ShortMessage sm) {
		this.sm = sm;
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public byte getIsReport() {
		return isReport;
	}

	public void setIsReport(byte isReport) {
		this.isReport = isReport;
	}

	public String getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(String recvTime) {
		this.recvTime = recvTime;
	}

	public String getSrcTermId() {
		return srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
	}

	public String getDstTermId() {
		return dstTermId;
	}

	public void setDstTermId(String dstTermId) {
		this.dstTermId = dstTermId;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	@Override
	public String dump() {
		String rt = "Deliver msg dump error..................";
		try {
			ByteBuffer contentBuffer = sm.getData();
			rt = "\r\nDeliver***************************************"
					  + "\r\nseqNo:             " + this.getSequenceNumber()
					  + "\r\nmsgId:             " + getMsgId()
					  + "\r\nisReport:          " + getIsReport()
					  + "\r\nrecvTime:          " + getRecvTime()
					  + "\r\nsrcTermID:         " + getSrcTermId()
					  + "\r\ndstTermId:         " + getDstTermId()
					  + "\r\nreserve:           " + getReserve()
					  + "\r\nmsgFormat:         " + getMsgFormat()
					  + "\r\nmsgLength:         " + getMsgLength()
					  + "\r\nmsgContent:        " + Arrays.toString(sm.getData().getBuffer());
					  if(getIsReport() == 1){
						      rt += "\r\n + Msg_Id:         " + contentBuffer.removeStringEx(10)
						          + "\r\n + sub             " + contentBuffer.removeStringEx(3)
						          + "\r\n + Dlvrd           " + contentBuffer.removeStringEx(3)
						          + "\r\n + Submit_date:    " + contentBuffer.removeStringEx(10)
						          + "\r\n + done_date:      " + contentBuffer.removeStringEx(10)
								  + "\r\n + Stat:           " + contentBuffer.removeStringEx(7)
								  + "\r\n + Err:            " + contentBuffer.removeStringEx(3)
								  + "\r\n + Txt:            " + contentBuffer.removeStringEx(20);
					  }else{
						  rt += "\r\nmsgContent_:       " + sm.getMessage();
					  }
					  rt += "\r\n****************************************Deliver";
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
		}
		return rt;
	}

	@Override
	public String name() {
		return "SMGP Deliver";
	}
}