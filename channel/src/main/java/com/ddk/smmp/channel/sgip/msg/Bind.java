package com.ddk.smmp.channel.sgip.msg;

import com.ddk.smmp.channel.sgip.exception.MSGException;
import com.ddk.smmp.channel.sgip.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.sgip.helper.ByteBuffer;
import com.ddk.smmp.channel.sgip.helper.SgipConstant;
import com.ddk.smmp.channel.sgip.msg.parent.Request;
import com.ddk.smmp.channel.sgip.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月26日 下午12:18:36 li_mr_ceo@163.com <br>
 * 
 */
public class Bind extends Request {
	private int loginType = 1;
	private String loginName = "";
	private String loginPwd = "";
	private String reserve = "";
	
	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	public Bind() {
		super(SgipConstant.CMD_BIND);
	}

	
	@Override
	protected Response createResponse() {
		return new BindResp();
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setLoginType(buffer.removeByte());
			setLoginName(buffer.removeStringEx(16));
			setLoginPwd(buffer.removeStringEx(16));
			setReserve(buffer.removeStringEx(8));
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendByte((byte) getLoginType());
		buffer.appendString(getLoginName(), 16);
		buffer.appendString(getLoginPwd(), 16);
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
		return "SGIP Bind";
	}

	@Override
	public String dump() {
		String rt = "\r\nBind************************************"
				  + "\r\nloginType:       " + loginType
				  + "\r\nloginName:       " + loginName
				  + "\r\nloginPwd:        " + loginPwd
				  + "\r\nreserve:         " + reserve
				  + "\r\n************************************Bind";
		return rt;
	}
}