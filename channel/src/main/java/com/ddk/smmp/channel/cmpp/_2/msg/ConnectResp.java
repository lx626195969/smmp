package com.ddk.smmp.channel.cmpp._2.msg;

import java.util.Arrays;

import com.ddk.smmp.channel.cmpp._2.exception.MSGException;
import com.ddk.smmp.channel.cmpp._2.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._2.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._2.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._2.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午04:53:12 li_mr_ceo@163.com <br>
 * 
 */
public class ConnectResp extends Response {
	/**
	 * 状态 0：正确 1：消息结构错 2：非法源地址 3：认证错 4：版本太高 5 ：其他错误
	 */
	private byte status = 0;

	/**
	 * ISMG认证码，用于鉴别ISMG。 其值通过单向MD5 hash计算得出，表示如下： <br>
	 * authServer =MD5（Status+authClient+shared secret）<br>
	 * Shared secret
	 * 由中国移动与源地址实体事先商定，authClient为源地址实体发送给ISMG的对应消息CMPP_Connect中的值。 认证出错时，此项为空。
	 */
	private String authServer = "";

	/**
	 * 服务器支持的最高版本号
	 */
	private byte version = 0;

	public ConnectResp() {
		super(CmppConstant.CMD_CONNECT_RESP);
	}

	public String getAuthServer() {
		return authServer;
	}

	public void setAuthServer(String authServer) {
		this.authServer = authServer;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
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
			setStatus(buffer.removeByte());
			setAuthServer(buffer.removeStringEx(16));
			setVersion(buffer.removeByte());
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendByte(getStatus());
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
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	@Override
	public String name() {
		return "CMPP ConnectResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nConnectResp******************************************"
				  + "\r\nstatus:        " + status
				  + "\r\nauthServer:    " + Arrays.toString(authServer.getBytes())
				  + "\r\nversion:       " + version
				  + "\r\n******************************************ConnectResp";
		return rt;
	}
}
