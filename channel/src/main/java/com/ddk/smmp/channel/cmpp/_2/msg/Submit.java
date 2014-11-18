package com.ddk.smmp.channel.cmpp._2.msg;

import com.ddk.smmp.channel.cmpp._2.exception.MSGException;
import com.ddk.smmp.channel.cmpp._2.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._2.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._2.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._2.helper.ShortMessage;
import com.ddk.smmp.channel.cmpp._2.msg.parent.Request;
import com.ddk.smmp.channel.cmpp._2.msg.parent.Response;
import com.ddk.smmp.channel.cmpp._2.utils.Tools;

/**
 * 
 * @author leeson 2014-6-9 下午05:44:48 li_mr_ceo@163.com <br>
 *         短消息提交请求<br>
 *         CMPP_SUBMIT操作的目的是SP在与ISMG建立应用层连接后向ISMG提交短信。
 *         ISMG以CMPP_SUBMIT_RESP消息响应。
 */
public class Submit extends Request {
	/** 信息标识 */
	private long msgId = 0l;
	/** 相同Msg_Id的信息总条数，从1开始 */
	private byte pkTotal = 1;
	/** 相同Msg_Id的信息序号，从1开始 */
	private byte pkNumber = 1;
	/**
	 * 是否要求返回状态确认报告： 0：不需要 1：需要
	 */
	private byte needReport = 1;
	/** 信息级别 */
	private byte priority = 0;
	/** 业务标识，是数字、字母和符号的组合 */
	private String serviceId = "";
	/**
	 * 计费用户类型字段： 0：对目的终端MSISDN计费； 1：对源终端MSISDN计费； 2：对SP计费；
	 * 3：表示本字段无效，对谁计费参见Fee_terminal_Id字段。
	 */
	private byte feeUserType = 0;
	/** 被计费用户的号码，当feeUserType为3时该值有效，当feeUserType为0、1、2时该值无意义。 */
	private String feeTermId = "";
	/** GSM协议类型 */
	private byte tpPid = 0;
	/** GSM协议类型 仅使用1位，右对齐 */
	private byte tpUdhi = 0;
	/** 短消息编码和内容 */
	private ShortMessage sm = new ShortMessage();
	/** 信息内容来源(SP_Id) */
	private String msgSrc = "";
	/**
	 * 资费类别： 01：对“计费用户号码”免费； 02：对“计费用户号码”按条计信息费； 03：对“计费用户号码”按包月收取信息费。
	 */
	private String feeType = "";
	/** 资费（以分为单位） */
	private String feeCode = "";
	/** 存活有效期 */
	private String validTime = "";
	/** 定时发送时间 */
	private String atTime = "";
	/**
	 * 源号码。SP的服务代码或前缀为服务代码的长号码,
	 * 网关将该号码完整的填到SMPP协议Submit_SM消息相应的source_addr字段，该号码最终在用户手机上显示为短消息的主叫号码。
	 */
	private String srcId = "";
	/** 接收信息的用户数量(小于100个用户) */
	private byte destTermIdCount = 0;
	/** 接收短信的MSISDN号码 */
	private String destTermId[] = new String[0];
	/** 保留 */
	private String reserve = "";

	public Submit() {
		super(CmppConstant.CMD_SUBMIT);
	}

	protected Response createResponse() {
		return new SubmitResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = Tools.bytesToLong(buffer.removeBytes(8).getBuffer());
			pkTotal = buffer.removeByte();
			pkNumber = buffer.removeByte();
			needReport = buffer.removeByte();
			priority = buffer.removeByte();
			serviceId = buffer.removeStringEx(10);
			feeUserType = buffer.removeByte();
			feeTermId = buffer.removeStringEx(21);
			tpPid = buffer.removeByte();
			tpUdhi = buffer.removeByte();
			byte msgFormat = buffer.removeByte();
			msgSrc = buffer.removeStringEx(6);
			feeType = buffer.removeStringEx(2);
			feeCode = buffer.removeStringEx(6);
			validTime = buffer.removeStringEx(17);
			atTime = buffer.removeStringEx(17);
			srcId = buffer.removeStringEx(21);
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
		buffer.appendBytes(Tools.longToBytes(msgId));
		buffer.appendByte(pkTotal);
		buffer.appendByte(pkNumber);
		buffer.appendByte(needReport);
		buffer.appendByte(priority);
		buffer.appendString(serviceId, 10);
		buffer.appendByte(feeUserType);
		buffer.appendString(feeTermId, 21);
		buffer.appendByte(tpPid);
		buffer.appendByte(tpUdhi);
		buffer.appendByte(sm.getMsgFormat());
		buffer.appendString(msgSrc, 6);
		buffer.appendString(feeType, 2);
		buffer.appendString(feeCode, 6);
		buffer.appendString(validTime, 17);
		buffer.appendString(atTime, 17);
		buffer.appendString(srcId, 21);
		buffer.appendByte((byte) destTermId.length);
		for (int i = 0; i < destTermId.length; i++)
			buffer.appendString(destTermId[i], 21);
		buffer.appendByte((byte)sm.getLength());
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
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	public String getAtTime() {
		return atTime;
	}

	public void setAtTime(String atTime) {
		this.atTime = atTime;
	}

	public String[] getDestTermId() {
		return destTermId;
	}

	public void setDestTermId(String[] destTermId) {
		this.destTermId = destTermId;
	}

	public byte getDestTermIdCount() {
		return destTermIdCount;
	}

	public void setDestTermIdCount(byte destTermIdCount) {
		this.destTermIdCount = destTermIdCount;
	}

	public String getFeeCode() {
		return feeCode;
	}

	public void setFeeCode(String feeCode) {
		this.feeCode = feeCode;
	}

	public String getFeeTermId() {
		return feeTermId;
	}

	public void setFeeTermId(String feeTermId) {
		this.feeTermId = feeTermId;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public byte getFeeUserType() {
		return feeUserType;
	}

	public void setFeeUserType(byte feeUserType) {
		this.feeUserType = feeUserType;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public String getMsgSrc() {
		return msgSrc;
	}

	public void setMsgSrc(String msgSrc) {
		this.msgSrc = msgSrc;
	}

	public byte getNeedReport() {
		return needReport;
	}

	public void setNeedReport(byte needReport) {
		this.needReport = needReport;
	}

	public byte getPkNumber() {
		return pkNumber;
	}

	public void setPkNumber(byte pkNumber) {
		this.pkNumber = pkNumber;
	}

	public byte getPkTotal() {
		return pkTotal;
	}

	public void setPkTotal(byte pkTotal) {
		this.pkTotal = pkTotal;
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

	public String getSrcId() {
		return srcId;
	}

	public void setSrcId(String srcId) {
		this.srcId = srcId;
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

	public String getValidTime() {
		return validTime;
	}

	public void setValidTime(String validTime) {
		this.validTime = validTime;
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
				  + "\r\nmsgId:             " + getMsgId()
				  + "\r\ntpUdhi:            " + getTpUdhi()
				  + "\r\nmsgSrc:            " + getMsgSrc()
				  + "\r\nsrcId:             " + getSrcId()
				  + "\r\ndestTermId:        " + getDestTermId()[0]
				  + "\r\nmsgFormat:         " + getMsgFormat()
				  + "\r\nmsgLength:         " + getMsgLength()
				  + "\r\nmsgContent:        " + new String(sm.getData().getBuffer())
				  + "\r\n***************************************Submit";
		return rt;
	}

	@Override
	public String name() {
		return "CMPP Submit";
	}
}