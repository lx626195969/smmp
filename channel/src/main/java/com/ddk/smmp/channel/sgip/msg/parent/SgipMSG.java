package com.ddk.smmp.channel.sgip.msg.parent;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ddk.smmp.channel.sgip.msg.header.SgipMSGHeader;
import com.ddk.smmp.channel.sgip.utils.Tools;

/**
 * 
 * @author leeson 2014-6-9 下午02:20:14 li_mr_ceo@163.com
 * 
 */
public abstract class SgipMSG extends MSG {
	public SgipMSGHeader header = null;

	public SgipMSG() {
		header = new SgipMSGHeader();
	}

	public SgipMSG(int commandId) {
		header = new SgipMSGHeader();
		header.setCommandId(commandId);
	}

	@Override
	public void assignSequenceNumber(int nodeId) {
		synchronized (this) {
			setSequenceNumber(nodeId, Tools.generateSeq());
		}
	}

	@Override
	public boolean equals(Object object) {
		if ((object != null) && (object instanceof SgipMSG)) {
			SgipMSG pdu = (SgipMSG) object;
			return pdu.getSequenceNumber1() == getSequenceNumber1()
					&& pdu.getSequenceNumber2() == getSequenceNumber2()
					&& pdu.getSequenceNumber3() == getSequenceNumber3();
		} else {
			return false;
		}
	}

	public String dump() {
		return name() + " dump() unimplemented";
	}

	/**
	 * 检查header是否为空,如果是就创建它
	 */
	private void checkHeader() {
		if (header == null) {
			header = new SgipMSGHeader();
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

	/** NODEID */
	@Override
	public int getSequenceNumber1() {
		checkHeader();
		return header.getSequenceNumber1();
	}

	/** MMddHHmmss */
	@Override
	public int getSequenceNumber2() {
		checkHeader();
		return header.getSequenceNumber2();
	}

	/** SEQ */
	@Override
	public int getSequenceNumber3() {
		checkHeader();
		return header.getSequenceNumber3();
	}

	public void setSequenceNumber(int nodeId, int time, int seq) {
		checkHeader();
		header.setSequenceNumber1(nodeId);
		header.setSequenceNumber2(time);
		header.setSequenceNumber3(seq);
	}

	public void setSequenceNumber(int nodeId, int seq) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");
		checkHeader();
		header.setSequenceNumber1(nodeId);
		header.setSequenceNumber2(Integer.parseInt(sdf.format(new Date())));
		header.setSequenceNumber3(seq);
	}

	public void setCommandLength(int cmdLen) {
		checkHeader();
		header.setCommandLength(cmdLen);
	}

	public void setCommandId(int cmdId) {
		checkHeader();
		header.setCommandId(cmdId);
	}
}