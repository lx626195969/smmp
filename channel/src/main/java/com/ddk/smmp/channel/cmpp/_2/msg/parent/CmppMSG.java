package com.ddk.smmp.channel.cmpp._2.msg.parent;

import com.ddk.smmp.channel.cmpp._2.msg.header.CmppMSGHeader;
import com.ddk.smmp.channel.cmpp._2.utils.Tools;


/**
 * 
 * @author leeson 2014-6-9 下午02:20:14 li_mr_ceo@163.com
 * 
 */
public abstract class CmppMSG extends MSG {
	public CmppMSGHeader header = null;

	public CmppMSG() {
		header = new CmppMSGHeader();
	}

	public CmppMSG(int commandId) {
		header = new CmppMSGHeader();
		header.setCommandId(commandId);
	}

	public void assignSequenceNumber() {
		synchronized (this) {
			setSequenceNumber(Tools.generateSeq());
		}
	}

	public boolean equals(Object object) {
		if ((object != null) && (object instanceof CmppMSG)) {
			CmppMSG pdu = (CmppMSG) object;
			return pdu.getSequenceNumber() == getSequenceNumber();
		} else {
			return false;
		}
	}

	public String getSequenceNumberAsString() {
		int data = header.getSequenceNumber();
		byte[] intBuf = new byte[4];
		intBuf[3] = (byte) (data & 0xff);
		intBuf[2] = (byte) ((data >>> 8) & 0xff);
		intBuf[1] = (byte) ((data >>> 16) & 0xff);
		intBuf[0] = (byte) ((data >>> 24) & 0xff);
		return new String(intBuf);
	}
	
	public String dump() {
		return name() + " dump() unimplemented";
	}

	/**
	 * 检查header是否为空,如果是就创建它
	 */
	private void checkHeader() {
		if (header == null) {
			header = new CmppMSGHeader();
		}
	}

	public int getCommandLength() {
		checkHeader();
		return header.getCommandLength();
	}

	public int getCommandId() {
		checkHeader();
		return header.getCommandId();
	}

	public int getSequenceNumber() {
		checkHeader();
		return header.getSequenceNumber();
	}

	public void setCommandLength(int cmdLen) {
		checkHeader();
		header.setCommandLength(cmdLen);
	}

	public void setCommandId(int cmdId) {
		checkHeader();
		header.setCommandId(cmdId);
	}

	public void setSequenceNumber(int seqNr) {
		checkHeader();
		header.setSequenceNumber(seqNr);
	}
}