package com.ddk.smmp.channel.cmpp._2.msg;

import java.util.Arrays;

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
 * @author leeson 2014-6-10 上午09:16:32 li_mr_ceo@163.com <br>
 * 
 */
public class Deliver extends Request {

	/**
	 * 信息标识 生成算法如下： 采用64位（8字节）的整数： 时间（格式为MMDDHHMMSS，即月日时分秒）：bit64~bit39，其中
	 * bit64~bit61：月份的二进制表示； bit60~bit56：日的二进制表示； bit55~bit51：小时的二进制表示；
	 * bit50~bit45：分的二进制表示； bit44~bit39：秒的二进制表示；
	 * 短信网关代码：bit38~bit17，把短信网关的代码转换为整数填写到该字段中。 序列号：bit16~bit1，顺序增加，步长为1，循环使用。
	 * 各部分如不能填满，左补零，右对齐。
	 */
	private long msgId = 0l;
	/**
	 * 目的号码 SP的服务代码，一般4--6位，或者是前缀为服务代码的长号码；该号码是手机用户短消息的被叫号码
	 */
	private String dstId = "";
	/** 业务类型，是数字、字母和符号的组合。 */
	private String serviceId = "";
	/** GSM协议类型。详细解释请参考GSM03.40中的9.2.3.9 */
	private byte tpPid = 0;
	/** GSM协议类型。详细解释请参考GSM03.40中的9.2.3.23，仅使用1位，右对齐 */
	private byte tpUdhi = 0;
	/** 短消息实体 */
	private ShortMessage sm = new ShortMessage();
	/** 源终端MSISDN号码（状态报告时填为CMPP_SUBMIT消息的目的终端号码） */
	private String srcTermId = "";
	/**
	 * 是否为状态报告： 0：非状态报告； 1：状态报告。
	 */
	private byte isReport = 0;
	/** 保留项 */
	private String reserved = "";

	public Deliver() {
		super(CmppConstant.CMD_DELIVER);
	}

	@Override
	protected Response createResponse() {
		return new DeliverResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = Tools.bytesToLong(buffer.removeBytes(8).getBuffer());
			dstId = buffer.removeStringEx(21);
			serviceId = buffer.removeStringEx(10);
			tpPid = buffer.removeByte();
			tpUdhi = buffer.removeByte();
			byte msgFormat = buffer.removeByte();
			srcTermId = buffer.removeStringEx(21);
			isReport = buffer.removeByte();
			byte signbyte = buffer.removeByte();
			int msgLength = signbyte < 0 ? signbyte + 256 : signbyte;
			if (msgLength > 0)
				sm.setData(buffer.removeBuffer(msgLength));
			sm.setMsgFormat(msgFormat);
			if(tpUdhi == 1){
				sm.setSuper(true);
			}
			reserved = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendBytes(Tools.longToBytes(msgId), 8);
		buffer.appendString(dstId, 21);
		buffer.appendString(serviceId, 10);
		buffer.appendByte(tpPid);
		buffer.appendByte(tpUdhi);
		buffer.appendByte(sm.getMsgFormat());
		buffer.appendString(srcTermId, 21);
		buffer.appendByte(isReport);
		buffer.appendByte((byte) sm.getLength());
		buffer.appendBuffer(sm.getData());
		buffer.appendString(reserved, 8);
		return buffer;
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
	}

	public String getDstId() {
		return dstId;
	}

	public void setDstId(String dstId) {
		this.dstId = dstId;
	}

	public byte getIsReport() {
		return isReport;
	}

	public void setIsReport(byte isReport) {
		this.isReport = isReport;
	}

	public String getReserved() {
		return reserved;
	}

	public void setReserved(String reserved) {
		this.reserved = reserved;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getSrcTermId() {
		return srcTermId;
	}

	public void setSrcTermId(String srcTermId) {
		this.srcTermId = srcTermId;
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

	@Override
	public String dump() {
		String rt = "Deliver msg dump error..................";
		try {
			ByteBuffer contentBuffer = sm.getData();
			rt = "\r\nDeliver***************************************"
					  + "\r\nseqNo:             " + this.getSequenceNumber()
					  + "\r\nmsgId:             " + getMsgId()
					  + "\r\ndstId:             " + getDstId()
					  + "\r\nserviceId:         " + getServiceId()
					  + "\r\ntpPid:             " + getTpPid()
					  + "\r\ntpUdhi:            " + getTpUdhi()
					  + "\r\nsrcTermId:         " + getSrcTermId()
					  + "\r\nisReport:          " + getIsReport()
					  + "\r\nmsgFormat:         " + getMsgFormat()
					  + "\r\nmsgLength:         " + getMsgLength()
					  + "\r\nmsgContent:        " + Arrays.toString(sm.getData().getBuffer());
					  if(getIsReport() == 1){
						      rt += "\r\n + Msg_Id:         " + Tools.bytesToLong(contentBuffer.removeBytes(8).getBuffer())
								  + "\r\n + Stat:           " + contentBuffer.removeStringEx(7)
								  + "\r\n + SubmitTime:     " + contentBuffer.removeStringEx(10)
								  + "\r\n + DoneTime:       " + contentBuffer.removeStringEx(10)
								  + "\r\n + DestTerminalId: " + contentBuffer.removeStringEx(21)
								  + "\r\n + SMSCSequence:   " + contentBuffer.removeInt();
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
		return "CMPP Deliver";
	}
}