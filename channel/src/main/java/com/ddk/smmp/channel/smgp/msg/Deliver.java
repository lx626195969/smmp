package com.ddk.smmp.channel.smgp.msg;

import java.io.UnsupportedEncodingException;

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
	private int msgFormat;
	private ShortMessage sm = new ShortMessage();
	private String recvTime = "";
	private String srcTermId = "";
	private String dstTermId = "";
	private int msgLength;
	private String content;
	private String reserve = "";
	private int pktotal = 1;
	private int pknumber = 1;
	private int tp_udhi;
	private int seqTemp = 0;
	
	private String report_msg_id;
	private String report_stat;
	
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
			msgFormat = buffer.removeByte();
			recvTime = buffer.removeStringEx(14);
			srcTermId = buffer.removeStringEx(21);
			dstTermId = buffer.removeStringEx(21);
			byte signbyte = buffer.removeByte();
			msgLength = signbyte < 0 ? signbyte + 256 : signbyte;
			byte[] contentByte = buffer.removeBuffer(msgLength).getBuffer();
			reserve = buffer.removeStringEx(8);
			
			if (msgFormat == 0) {
				try {
					content = new String(contentByte, 0, msgLength, "ISO8859_1").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if (msgFormat == 8) {
				try {
					byte[] head = new byte[6];
					System.arraycopy(contentByte, 0, head, 0, 6);
					if (head[0]==5) {
						content = new String(contentByte, 6, msgLength - 6, "UnicodeBigUnmarked").trim();
						this.tp_udhi = 1;
						this.pktotal = head[4];
						this.pknumber = head[5];
						this.seqTemp = head[3];
					} else {
						content = new String(contentByte, 0, msgLength, "UnicodeBigUnmarked").trim();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else {
				try {
					content = new String(contentByte, 0, msgLength, "GBK").trim();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			if (isReport == 1) {
				try {
					int pos = content.indexOf("id:");
					byte[] msgBytes = null;
					if (msgFormat == 0) {
						msgBytes = content.substring(pos + 3, pos + 13).getBytes("ISO8859_1");
					}else if (msgFormat == 8) {
						msgBytes = content.substring(pos + 3, pos + 13).getBytes("UnicodeBigUnmarked");
					}else{
						msgBytes = content.substring(pos + 3, pos + 13).getBytes("GBK");
					}
					report_msg_id = Tools.resolveSMGP_MsgId(msgBytes);
					pos = content.indexOf("stat:");
					report_stat = content.substring(pos + 5, pos + 12);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
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
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPktotal() {
		return pktotal;
	}

	public void setPktotal(int pktotal) {
		this.pktotal = pktotal;
	}

	public int getPknumber() {
		return pknumber;
	}

	public void setPknumber(int pknumber) {
		this.pknumber = pknumber;
	}

	public int getTp_udhi() {
		return tp_udhi;
	}

	public void setTp_udhi(int tp_udhi) {
		this.tp_udhi = tp_udhi;
	}

	public int getSeqTemp() {
		return seqTemp;
	}

	public void setSeqTemp(int seqTemp) {
		this.seqTemp = seqTemp;
	}

	public void setMsgFormat(int msgFormat) {
		this.msgFormat = msgFormat;
	}

	public void setMsgLength(int msgLength) {
		this.msgLength = msgLength;
	}
	
	public String getReport_msg_id() {
		return report_msg_id;
	}

	public void setReport_msg_id(String report_msg_id) {
		this.report_msg_id = report_msg_id;
	}

	public String getReport_stat() {
		return report_stat;
	}

	public void setReport_stat(String report_stat) {
		this.report_stat = report_stat;
	}

	@Override
	public String dump() {
		String rt = "Deliver msg dump error..................";
			rt = "\r\nDeliver***************************************"
					  + "\r\nseqNo:             " + this.getSequenceNumber()
					  + "\r\nmsgId:             " + this.msgId
					  + "\r\nisReport:          " + this.isReport
					  + "\r\nrecvTime:          " + this.recvTime
					  + "\r\nsrcTermID:         " + this.srcTermId
					  + "\r\ndstTermId:         " + this.dstTermId
					  + "\r\nreserve:           " + this.reserve
					  + "\r\nmsgFormat:         " + this.msgFormat
					  + "\r\nmsgLength:         " + this.msgLength
					  + "\r\nmsgContent:        " + this.content;
			
					  if(getIsReport() == 1){
						      rt += "\r\n + Id:             " + this.report_msg_id
								  + "\r\n + Stat:           " + this.report_stat;
					  }else{
						  rt += "\r\n + msg_content:             " + this.content
							  + "\r\n + pkNumber:                " + this.pknumber
							  + "\r\n + pkTotle:                " + this.pktotal;
					  }
					  rt += "\r\n****************************************Deliver";
		return rt;
	}

	@Override
	public String name() {
		return "SMGP Deliver";
	}
	
	protected static int byte4ToInteger(byte[] b, int offset) {
		return (0xff & b[offset]) << 24 | (0xff & b[offset + 1]) << 16
				| (0xff & b[offset + 2]) << 8 | (0xff & b[offset + 3]);
	}
}