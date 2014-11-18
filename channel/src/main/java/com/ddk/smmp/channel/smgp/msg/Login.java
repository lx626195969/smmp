package com.ddk.smmp.channel.smgp.msg;

import java.security.MessageDigest;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.smgp.helper.ByteBuffer;
import com.ddk.smmp.channel.smgp.helper.SmgpConstant;
import com.ddk.smmp.channel.smgp.msg.parent.Request;
import com.ddk.smmp.channel.smgp.msg.parent.Response;

/**
 * 
 * @author leeson 2014年6月25日 上午10:32:29 li_mr_ceo@163.com <br>
 *
 */
public class Login extends Request {
	private String clientId = "";
	private byte[] authClient = new byte[16];
	private int loginMode = 0;//0＝发送短消息（send mode）； 1＝接收短消息（receive mode）； 2＝收发短消息（transmit mode）；
	private int timeStamp = 0;
	private int version = 0x30;//int值48
	
	private String sharedSecret = "";
	
	public Login() {
		super(SmgpConstant.CMD_LOGIN);
	}

	public Login(byte version) {
		super(SmgpConstant.CMD_LOGIN);
		setVersion(version);
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setClientId(buffer.removeStringEx(8));
			setAuthClient(buffer.removeBytes(16).getBuffer());
			setLoginMode(buffer.removeByte());
			setTimeStamp(buffer.removeInt());
			setVersion(buffer.removeByte());
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		
		buffer.appendString(getClientId(), 8);
		buffer.appendBytes(getAuthClient(), 16);
		buffer.appendByte((byte)getLoginMode());
		buffer.appendInt(getTimeStamp());
		buffer.appendByte((byte)getVersion());
		
		return buffer;
	}

	public byte[] genAuthClient() {
		byte[] result = new byte[16];
		try {
			ByteBuffer buffer = new ByteBuffer();
			buffer.appendString(clientId, clientId.length());
			byte[] ba = new byte[7];
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public byte[] getAuthClient() {
		return authClient;
	}

	public void setAuthClient(byte[] authClient) {
		this.authClient = authClient;
	}

	public int getLoginMode() {
		return loginMode;
	}

	public void setLoginMode(int loginMode) {
		this.loginMode = loginMode;
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
		return new LoginResp();
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
		return "SMGP Login";
	}

	@Override
	public String dump() {
		String rt = "\r\nLogin************************************"
				  + "\r\nclientId:       " + clientId
				  + "\r\nauthClient:     " + Arrays.toString(authClient)
				  + "\r\nloginMode:      " + loginMode
				  + "\r\ntimeStamp:      " + timeStamp
				  + "\r\nversion:        " + version
				  + "\r\nsharedSecret:   " + sharedSecret
				  + "\r\n************************************Login";
		return rt;
	}
}