package com.ddk.smmp.channel.smgp.msg;

import java.io.UnsupportedEncodingException;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.ShortMessage;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Request;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月25日 上午10:33:00 li_mr_ceo@163.com <br>
 * 
 */
public class Submit extends Request {
	private byte msgType = 0x06;
	private byte needReport = 0x01;
	private byte priority = 0;
	private String serviceId = "PC2P";
	private String feeType = "01";
	private String feeCode = "0";
	private String fixedFee = "0";
	private ShortMessage sm = new ShortMessage();
	private String validTime = "";
	private String atTime = "";
	private String srcTermId = "";
	private String chargeTermId = "";
	private byte destTermIdCount = 0;
	private String destTermId[] = new String[0];
	private String reserve = "";
	
	private boolean isSuper = false;
	private int pkTotle;
	private int pkNumber;
	
	private String spId;//企业ID
	
	public Submit() {
		super(SmgpConstant.CMD_SUBMIT);
	}

	protected Response createResponse() {
		return new SubmitResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgType = buffer.removeByte();
			needReport = buffer.removeByte();
			priority = buffer.removeByte();
			serviceId = buffer.removeStringEx(10);
			feeType = buffer.removeStringEx(2);
			feeCode = buffer.removeStringEx(6);
			fixedFee = buffer.removeStringEx(6);
			byte msgFormat = buffer.removeByte();
			validTime = buffer.removeStringEx(17);
			atTime = buffer.removeStringEx(17);
			srcTermId = buffer.removeStringEx(21);
			chargeTermId = buffer.removeStringEx(21);
			destTermIdCount = buffer.removeByte();
			destTermId = new String[destTermIdCount];
			for (int i = 0; i < destTermIdCount; i++)
				destTermId[i] = buffer.removeStringEx(21);
			byte signbyte = buffer.removeByte();
			int msgLength = signbyte < 0 ? signbyte + 256 : signbyte;
			sm.setData(buffer.removeBuffer(msgLength));
			sm.setMsgFormat(msgFormat);
			reserve = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}
	
	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendByte(msgType);
		buffer.appendByte(needReport);
		buffer.appendByte(priority);
		buffer.appendString(serviceId, 10);
		buffer.appendString(feeType, 2);
		buffer.appendString(feeCode, 6);
		buffer.appendString(fixedFee, 6);
		buffer.appendByte(sm.getMsgFormat());
		buffer.appendString(validTime, 17);
		buffer.appendString(atTime, 17);
		buffer.appendString(srcTermId, 21);
		buffer.appendString(chargeTermId, 21);
		buffer.appendByte((byte) destTermId.length);
		for (int i = 0; i < destTermId.length; i++)
			buffer.appendString(destTermId[i], 21);
		buffer.appendByte((byte) sm.getLength());
		buffer.appendBuffer(sm.getData());
		buffer.appendString(reserve, 8);
		
		byte[] temp = null;
		if(isSuper){
			/*============处理长短信的TP_udhi============*/
			temp = integerToByte(0x0002);
			buffer.appendByte(temp[2]);
			buffer.appendByte(temp[3]);
			temp = integerToByte(1);
			buffer.appendByte(temp[2]);
			buffer.appendByte(temp[3]);
			temp = integerToByte(1);
			buffer.appendByte(temp[3]);
			/*============处理长短信的pkTotle============*/
			temp = integerToByte(0x0010);
			buffer.appendByte(temp[2]);
			buffer.appendByte(temp[3]);
			temp = integerToByte(1);
			buffer.appendByte(temp[2]);
			buffer.appendByte(temp[3]);
			temp = integerToByte(pkTotle);
			buffer.appendByte(temp[3]);
			/*============处理长短信的pkNumber============*/
			temp = integerToByte(0x000a);
			buffer.appendByte(temp[2]);
			buffer.appendByte(temp[3]);
			temp = integerToByte(1);
			buffer.appendByte(temp[2]);
			buffer.appendByte(temp[3]);
			temp = integerToByte(pkNumber);
			buffer.appendByte(temp[3]);
		}
		
		//msg_src
		temp = integerToByte(0x0010);
		buffer.appendByte(temp[2]);
		buffer.appendByte(temp[3]);
		//
		temp = integerToByte(8);
		buffer.appendByte(temp[2]);
		buffer.appendByte(temp[3]);
		
		//SP_ID
		temp = new byte[8];
		byte[] tm = null;
		try {
			tm = spId.getBytes("GBK");
			System.arraycopy(tm, 0, tm, 0, tm.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		buffer.appendBytes(temp);
		
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
		header.setCommandLength(SmgpConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	public String getSpId() {
		return spId;
	}

	public void setSpId(String spId) {
		this.spId = spId;
	}

	public boolean isSuper() {
		return isSuper;
	}

	public void setSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}
	
	public int getPkTotle() {
		return pkTotle;
	}

	public void setPkTotle(int pkTotle) {
		this.pkTotle = pkTotle;
	}

	public int getPkNumber() {
		return pkNumber;
	}

	public void setPkNumber(int pkNumber) {
		this.pkNumber = pkNumber;
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public byte getNeedReport() {
		return needReport;
	}

	public void setNeedReport(byte needReport) {
		this.needReport = needReport;
	}

	public byte getPriority() {
		return priority;
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getFixedFee() {
		return fixedFee;
	}

	public void setFixedFee(String fixedFee) {
		this.fixedFee = fixedFee;
	}

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
	}

	public String getAtTime() {
		return atTime;
	}

	public void setAtTime(String atTime) {
		this.atTime = atTime;
	}

	public String getSrcTermId() {
		return srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
	}

	public String getChargeTermId() {
		return chargeTermId;
	}

	public void setChargeTermId(String chargeTermId) {
		this.chargeTermId = chargeTermId;
	}

	public byte getDestTermIdCount() {
		return destTermIdCount;
	}

	public void setDestTermIdCount(byte destTermIdCount) {
		this.destTermIdCount = destTermIdCount;
	}

	public String[] getDestTermId() {
		return destTermId;
	}

	public void setDestTermId(String[] destTermId) {
		this.destTermId = destTermId;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
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

	public void setSm(ShortMessage sm) {
		this.sm = sm;
	}

	public ShortMessage getSm() {
		return this.sm;
	}

	@Override
	public String dump() {
		String rt = "\r\nSubmit**************************************"
				  + "\r\nmsgType:             " + getMsgType()
				  + "\r\nneedReport:          " + getNeedReport()
				  + "\r\npriority             " + getPriority()
				  + "\r\nserviceId            " + getServiceId()
			      + "\r\nfeeType              " + getFeeType()
			      + "\r\nfeeCode              " + getFeeCode()
			      + "\r\nfixedFee             " + getFixedFee()
			      + "\r\nvalidTime            " + getValidTime()
			      + "\r\natTime               " + getAtTime()
			      + "\r\nsrcTermId            " + getSrcTermId()
			      + "\r\nchargeTermId         " + getChargeTermId()
			      + "\r\ndestTermIdCount      " + getDestTermIdCount()
			      + "\r\ndestTermId           " + getDestTermId()[0]
			      + "\r\nmsgFormat:           " + getMsgFormat()
			      + "\r\nmsgLength:           " + getMsgLength()
			      + "\r\nmsgContent:          " + new String(sm.getData().getBuffer())
			      + "\r\nreserve              " + getReserve()
				  + "\r\n***************************************Submit";
		return rt;
	}

	@Override
	public String name() {
		return "SMGP Submit";
	}
	
	/**
	 * convert a integer to 4 bytes
	 * 
	 * @param n
	 *            the integer want to be converted to bytes
	 * @return byte array sorted from height to low, the size is 4
	 */
	protected static byte[] integerToByte(int n) {
		byte b[] = new byte[4];
		b[0] = (byte) (n >> 24);
		b[1] = (byte) (n >> 16);
		b[2] = (byte) (n >> 8);
		b[3] = (byte) n;
		return b;
	}
}