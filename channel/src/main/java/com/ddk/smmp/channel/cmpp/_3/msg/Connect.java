package com.ddk.smmp.channel.cmpp._3.msg;

import java.security.MessageDigest;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Request;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-9 下午02:54:50 li_mr_ceo@163.com <br>
 *         CMPP_CONNECT操作的目的是SP向ISMG注册作为一个合法SP身份
 *         ，若注册成功后即建立了应用层的连接，此后SP可以通过此ISMG接收和发送短信。
 *         ISMG以CMPP_CONNECT_RESP消息响应SP的请求
 */
public class Connect extends Request {
	/** 源地址，即企业代码 */
	private String srcAddr = "";

	/**
	 * 用于鉴别源地址。其值通过单向MD5 hash计算得出<br>
	 * 表示如下： AuthenticatorSource = MD5（srcAddr+9 字节的0 +shared secret+timestamp）
	 * Shared secret 由中国移动与企业事先商定，timestamp格式为：MMDDHHMMSS，即月日时分秒，10位。
	 */
	private byte[] authClient = new byte[16];

	/** 双方协商的版本号(高位4bit表示主版本号,低位4bit表示次版本号) */
	private int version = 0;

	/** 时间戳的明文,由客户端产生,格式为MMDDHHMMSS，即月日时分秒，10位数字的整型，右对齐 。 */
	private int timeStamp = 0;

	private String sharedSecret = "";

	public Connect() {
		super(CmppConstant.CMD_CONNECT);
	}

	public Connect(byte version) {
		super(CmppConstant.CMD_CONNECT);
		setVersion(version);
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setSrcAddr(buffer.removeStringEx(6));
			setAuthClient(buffer.removeBytes(16).getBuffer());
			setVersion(buffer.removeByte());
			setTimeStamp(buffer.removeInt());
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(getSrcAddr(), 6);
		buffer.appendBytes(getAuthClient(), 16);
		buffer.appendByte((byte)getVersion());
		buffer.appendInt(getTimeStamp());
		return buffer;
	}

	public byte[] genAuthClient() {
		byte[] result = new byte[16];
		try {
			ByteBuffer buffer = new ByteBuffer();
			buffer.appendString(srcAddr, srcAddr.length());
			byte[] ba = new byte[9];
			buffer.appendBytes(ba);
			buffer.appendString(sharedSecret, sharedSecret.length());
			String timeStamp = "" + getTimeStamp();
			for (int i = 10 - timeStamp.length(); i > 0; i--)
				timeStamp = "0" + timeStamp;
			buffer.appendString(timeStamp, timeStamp.length());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			result = md5.digest(buffer.getBuffer());
		} catch (Exception ex) {
			logger.info("Failed genAuthClient!");
		}
		return result;
	}

	public int genTimeStamp() {
		Date date = new Date();
		Format formatter = new SimpleDateFormat("MMddHHmmss");
		int timeStamp = Integer.parseInt(formatter.format(date), 10);
		return timeStamp;
	}

	public byte[] getAuthClient() {
		return authClient;
	}

	public void setAuthClient(byte[] authClient) {
		this.authClient = authClient;
	}

	public String getSrcAddr() {
		return srcAddr;
	}

	public void setSrcAddr(String srcAddr) {
		this.srcAddr = srcAddr;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(int timeStamp) {
		this.timeStamp = timeStamp;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSharedSecret() {
		return sharedSecret;
	}

	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}

	protected Response createResponse() {
		return new ConnectResp();
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
		return "CMPP Connect";
	}

	@Override
	public String dump() {
		String rt = "\r\nConnect************************************"
				  + "\r\nsrcAddr:       " + srcAddr
				  + "\r\nauthClient:    " + Arrays.toString(authClient)
		          + "\r\nversion:       " + version
				  + "\r\ntimeStamp:     " + timeStamp
				  + "\r\nsharedSecret:  " + sharedSecret
				  + "\r\n************************************Connect";
		return rt;
	}
}