package com.ddk.smmp.channel.sgip.msg;

import java.util.Date;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.Request;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月26日 下午2:43:51 li_mr_ceo@163.com <br>
 *
 */
public class Report extends Request {
	private int seq1 = 0;
	private int seq2 = 0;
	private int seq3 = 0;
	private int reportType = 0;
	private String userNumber = "";
	private int state = 0;
	private int errorCode = 0;
	private String reserve = "";
	
	private Date receiveDate;//用作生成报告获取时间
	public Date getReceiveDate() {
		return receiveDate;
	}
	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	public int getSeq1() {
		return seq1;
	}
	
	public void setSeq1(int seq1) {
		this.seq1 = seq1;
	}
	
	public int getSeq2() {
		return seq2;
	}
	
	public void setSeq2(int seq2) {
		this.seq2 = seq2;
	}
	
	public int getSeq3() {
		return seq3;
	}
	
	public void setSeq3(int seq3) {
		this.seq3 = seq3;
	}
	
	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getUserNumber() {
		return userNumber;
	}

	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public Report() {
		super(SgipConstant.CMD_REPORT);
	}

	
	@Override
	protected Response createResponse() {
		return new ReportResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setSeq1(buffer.removeInt());
			setSeq2(buffer.removeInt());
			setSeq3(buffer.removeInt());
			setReportType(buffer.removeByte());
			setUserNumber(buffer.removeStringEx(21));
			setState(buffer.removeByte());
			setErrorCode(buffer.removeByte());
			setReserve(buffer.removeStringEx(8));
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(seq1);
		buffer.appendInt(seq2);
		buffer.appendInt(seq3);
		buffer.appendByte((byte)reportType);
		buffer.appendString(userNumber, 21);
		buffer.appendByte((byte)state);
		buffer.appendByte((byte)errorCode);
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

	@Override
	public String name() {
		return "SGIP Report";
	}

	@Override
	public String dump() {
		String rt = "\r\nReport************************************"
				  + "\r\nseq:          " + seq1 + "|" + seq2 + "|" + seq3
				  + "\r\nreportType:   " + reportType
				  + "\r\nuserNumber:   " + userNumber
				  + "\r\nstate:        " + state
				  + "\r\nerrorCode:    " + errorCode
				  + "\r\nreserve:      " + reserve
				  + "\r\n************************************Report";
		return rt;
	}
}