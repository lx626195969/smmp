package com.ddk.smmp.channel.smgp.helper;

import java.io.UnsupportedEncodingException;

import com.ddk.smmp.channel.smgp.exception.MSGException;
import com.ddk.smmp.channel.smgp.msg.parent.ByteData;

public class ShortMessage extends ByteData {

	/**
	 * 信息格式： 0：ASCII串； 3：短信写卡操作； 4：二进制信息； 8：UCS2编码； 15：含GB汉字 <br>
	 * 我们只关心 0、8、15
	 */
	byte msgFormat = 15;

	/** 信息内容 */
	byte[] messageData = null;

	String encoding = "US-ASCII";

	boolean isSuper = false;

	public boolean isSuper() {
		return isSuper;
	}

	public void setSuper(boolean isSuper) {
		this.isSuper = isSuper;
	}
	
	@Override
	public ByteBuffer getData() {
		ByteBuffer buffer = new ByteBuffer(messageData);
		return buffer;
	}

	public void setMessage(byte[] messageData, byte msgFormat) {
		this.messageData = messageData;
		this.msgFormat = msgFormat;
		setMsgFormat(msgFormat);
	}

	public void setMessage(String msg, byte msgFormat) {
		setMsgFormat(msgFormat);
		try {
			this.messageData = msg.getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			logger.warn("unsupportted msgFormat!", e);
		}
	}

	public String getMessage() {
		String str = "";
		try {
			if(isSuper){
				byte[] finalContent = new byte[messageData.length - 6];
				System.arraycopy(messageData, 6, finalContent, 0, messageData.length - 6);
				str = new String(finalContent, encoding);
			}else{
				str = new String(messageData, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {

		}
		return str;
	}

	public int getLength() {
		return messageData == null ? 0 : messageData.length;
	}

	public String dump() {
		String rt = "\r\nShortMessage==========" 
			      + "\r\nmsgFormat:     " + msgFormat
				  + "\r\nmsg:           " + getMessage() 
				  + "\r\n==========ShortMessage";
		return rt;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		this.messageData = buffer.getBuffer();
	}

	public void setData(byte[] data) {
		this.messageData = data;
	}

	public byte getMsgFormat() {
		return msgFormat;
	}

	public void setMsgFormat(byte msgFormat) {
		this.msgFormat = msgFormat;
		if (msgFormat == 0) {
			encoding = "US-ASCII";
		} else if (msgFormat == 8) {
			encoding = "UnicodeBigUnmarked";
		} else if (msgFormat == 15) {
			encoding = "GBK";
		}
	}

	public void setSm(String msg, byte msgFormat) {
		setMsgFormat(msgFormat);
		try {
			setData(msg.getBytes(encoding));
		} catch (UnsupportedEncodingException e) {
			logger.warn("msgFormat unsupportted!", e);
		}
	}
}
