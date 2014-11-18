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
 * @author leeson 2014年6月26日 下午12:47:33 li_mr_ceo@163.com <br>
 *
 */
public class Submit extends Request {
	private String spNumber = "";
	private String chargeNumber = "000000000000000000000";
	private int userCount = 0;
	private String userNumber[] = new String[0];
	private String corpId = "";
	private String serviceType = "";
	private int feeType = 1;
	private String feeValue = "";
	private String givenValue = "";
	private int agentFlag = 2;
	private int mtFlag = 1;
	private int priority = 0;
	private String valid_time = "";
	private String at_time = "";
	private int needReport = 1;
	private int tpPid = 0;
	private int tpUdhi = 0;
	private ShortMessage sm = new ShortMessage();
	private int msgType = 0;
	private String reserve = "";

	public Submit() {
		super(SgipConstant.CMD_SUBMIT);
	}

	protected Response createResponse() {
		return new SubmitResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			spNumber = buffer.removeStringEx(21);
			chargeNumber = buffer.removeStringEx(21);
			userCount = buffer.removeByte();
			for(int i = 0;i < userCount;i++){
				userNumber[i] = buffer.removeStringEx(21);
			}
			corpId = buffer.removeStringEx(5);
			serviceType = buffer.removeStringEx(10);
			feeType = buffer.removeByte();
			feeValue = buffer.removeStringEx(6);
			givenValue = buffer.removeStringEx(6);
			agentFlag = buffer.removeByte();
			mtFlag = buffer.removeByte();
			priority = buffer.removeByte();
			valid_time = buffer.removeStringEx(16);
			at_time = buffer.removeStringEx(16);
			needReport = buffer.removeByte();
			tpPid = buffer.removeByte();
			tpUdhi = buffer.removeByte();
			byte msgFormat = buffer.removeByte();
			msgType = buffer.removeByte();
			int msgLength = buffer.removeInt();
			sm.setData(buffer.removeBuffer(msgLength));
			sm.setMsgFormat(msgFormat);
			reserve = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(spNumber, 21);
		buffer.appendString(chargeNumber, 21);
		buffer.appendByte((byte)userNumber.length);
		for (int i = 0; i < userNumber.length; i++)
			buffer.appendString(userNumber[i], 21);
		buffer.appendString(corpId, 5);
		buffer.appendString(serviceType, 10);
		buffer.appendByte((byte)feeType);
		buffer.appendString(feeValue, 6);
		buffer.appendString(givenValue, 6);
		buffer.appendByte((byte)agentFlag);
		buffer.appendByte((byte)mtFlag);
		buffer.appendByte((byte)priority);
		buffer.appendString(valid_time, 16);
		buffer.appendString(at_time, 16);
		buffer.appendByte((byte)needReport);
		buffer.appendByte((byte)tpPid);
		buffer.appendByte((byte)tpUdhi);
		buffer.appendByte(sm.getMsgFormat());
		buffer.appendByte((byte)msgType);
		buffer.appendInt(sm.getLength());
		buffer.appendBuffer(sm.getData());
		buffer.appendString(reserve, 8);
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
	
	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public String getChargeNumber() {
		return chargeNumber;
	}

	public void setChargeNumber(String chargeNumber) {
		this.chargeNumber = chargeNumber;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

	public String[] getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String[] userNumber) {
		this.userNumber = userNumber;
	}

	public String getCorpId() {
		return corpId;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public int getFeeType() {
		return feeType;
	}

	public void setFeeType(int feeType) {
		this.feeType = feeType;
	}

	public String getFeeValue() {
		return feeValue;
	}

	public void setFeeValue(String feeValue) {
		this.feeValue = feeValue;
	}

	public String getGivenValue() {
		return givenValue;
	}

	public void setGivenValue(String givenValue) {
		this.givenValue = givenValue;
	}

	public int getAgentFlag() {
		return agentFlag;
	}

	public void setAgentFlag(int agentFlag) {
		this.agentFlag = agentFlag;
	}

	public int getMtFlag() {
		return mtFlag;
	}

	public void setMtFlag(int mtFlag) {
		this.mtFlag = mtFlag;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getValid_time() {
		return valid_time;
	}

	public void setValid_time(String valid_time) {
		this.valid_time = valid_time;
	}

	public String getAt_time() {
		return at_time;
	}

	public void setAt_time(String at_time) {
		this.at_time = at_time;
	}

	public int getNeedReport() {
		return needReport;
	}

	public void setNeedReport(int needReport) {
		this.needReport = needReport;
	}

	public int getTpPid() {
		return tpPid;
	}

	public void setTpPid(int tpPid) {
		this.tpPid = tpPid;
	}

	public int getTpUdhi() {
		return tpUdhi;
	}

	public void setTpUdhi(int tpUdhi) {
		this.tpUdhi = tpUdhi;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	@Override
	public String dump() {
		String rt = "\r\nSubmit**************************************"
				  + "\r\nspNumber:             " + spNumber
				  + "\r\nchargeNumber:         " + chargeNumber
				  + "\r\nuserCount:            " + userCount
				  + "\r\nuserNumber:           " + userNumber[0]
				  + "\r\ncorpId:               " + corpId
				  + "\r\nserviceType:          " + serviceType
				  + "\r\nfeeType:              " + feeType
				  + "\r\nfeeValue:             " + feeValue
				  + "\r\ngivenValue:           " + givenValue
				  + "\r\nagentFlag:            " + agentFlag
				  + "\r\nmtFlag:               " + mtFlag
				  + "\r\npriority:             " + priority
				  + "\r\nvalid_time:           " + valid_time
				  + "\r\nat_time:              " + at_time
				  + "\r\nneedReport:           " + needReport
				  + "\r\ntpPid:                " + tpPid
				  + "\r\ntpUdhi:               " + tpUdhi
				  + "\r\nmsgFmt:               " + getMsgFormat()
				  + "\r\nmsgLength:            " + getMsgLength()
				  + "\r\nmsgContent:           " + new String(sm.getData().getBuffer())
				  + "\r\nmsgType:              " + msgType
				  + "\r\nreserve:              " + reserve
				  + "\r\n***************************************Submit";
		return rt;
	}

	@Override
	public String name() {
		return "SGIP Submit";
	}
}