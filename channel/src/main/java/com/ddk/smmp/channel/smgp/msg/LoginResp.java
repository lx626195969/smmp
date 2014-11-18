package com.ddk.smmp.channel.smgp.msg;

import java.util.Arrays;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月25日 上午10:32:35 li_mr_ceo@163.com <br>
 *
 */
public class LoginResp extends Response {
	private int status = 0;

	private String authServer = "";

	private byte version = 0;

	public LoginResp() {
		super(SmgpConstant.CMD_LOGIN_RESP);
	}

	public String getAuthServer() {
		return authServer;
	}

	public void setAuthServer(String authServer) {
		this.authServer = authServer;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public byte getVersion() {
		return version;
	}

	public void setVersion(byte version) {
		this.version = version;
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setStatus(buffer.removeInt());
			setAuthServer(buffer.removeStringEx(16));
			setVersion(buffer.removeByte());
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendInt(getStatus());
		buffer.appendString(getAuthServer(), 16);
		buffer.appendByte(getVersion());
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

	@Override
	public String name() {
		return "SMGP LoginResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nLoginResp******************************************"
				  + "\r\nstatus:        " + status
				  + "\r\nauthServer:    " + Arrays.toString(authServer.getBytes())
				  + "\r\nversion:       " + version
				  + "\r\n******************************************LoginResp";
		return rt;
	}
}
