package com.sioo.cmppgw.entity;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * 
 * @author leeson 2014年8月22日 上午9:18:57 li_mr_ceo@163.com <br>
 *
 */
public abstract class CmppHead implements Serializable, Cloneable {
	private static final long serialVersionUID = -1431390763422874682L;

	protected int totalLength;
	protected int commandId;
	protected int secquenceId;

	protected int protocalType;
	protected byte[] msgBytes;

	public int getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(int totalLength) {
		this.totalLength = totalLength;
	}

	public int getCommandId() {
		return commandId;
	}

	public void setCommandId(int commandId) {
		this.commandId = commandId;
	}

	public int getSecquenceId() {
		return secquenceId;
	}

	public void setSecquenceId(int secquenceId) {
		this.secquenceId = secquenceId;
	}

	/**
	 * 子类字节获取，要负责父类中三属性数据生成
	 * 
	 * @param bb
	 * @return
	 */
	protected abstract void doSubEncode(ByteBuffer bb);

	/**
	 * 子类解码，被父类回调
	 * 
	 * @param bb
	 */
	protected abstract void doSubDecode(ByteBuffer bb);

	/**
	 * 对象编码为字节数组
	 * 
	 * @return
	 */
	public byte[] doEncode() {
		processHead();
		ByteBuffer bb = ByteBuffer.allocate(totalLength);
		bb.putInt(totalLength);
		bb.putInt(commandId);
		bb.putInt(secquenceId);
		doSubEncode(bb);
		this.msgBytes = bb.array();
		return bb.array();
	}

	protected abstract void processHead();

	/**
	 * 字节数组解码为对象
	 * 
	 * @param bytes
	 * @return
	 */
	public void doDecode(byte[] bytes) {
		this.msgBytes = bytes;
		doDecode();
	}

	public void doDecode() {
		if (msgBytes == null) {
			throw new RuntimeException("Object Bytes is Null");
		}
		ByteBuffer bb = ByteBuffer.wrap(msgBytes);
		totalLength = bb.getInt();
		commandId = bb.getInt();
		secquenceId = bb.getInt();
		doSubDecode(bb);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		CmppHead cmppHead = (CmppHead) o;

		if (commandId != cmppHead.commandId) {
			return false;
		}
		if (protocalType != cmppHead.protocalType) {
			return false;
		}
		if (secquenceId != cmppHead.secquenceId) {
			return false;
		}
		if (totalLength != cmppHead.totalLength) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = totalLength;
		result = 31 * result + commandId;
		result = 31 * result + secquenceId;
		result = 31 * result + protocalType;
		return result;
	}

	protected byte[] getHead() {
		byte[] head = new byte[12];
		ByteBuffer byteBuffer = ByteBuffer.wrap(head);
		byteBuffer.putInt(totalLength);
		byteBuffer.putInt(commandId);
		byteBuffer.putInt(secquenceId);
		return byteBuffer.array();
	}

	protected void setHead(byte[] bytes) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		totalLength = byteBuffer.getInt();
		commandId = byteBuffer.getInt();
		secquenceId = byteBuffer.getInt();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("CmppHead{");
		sb.append("totalLength=").append(totalLength);
		sb.append(", commandId=").append(commandId);
		sb.append(", secquenceId=").append(secquenceId);
		sb.append(", protocalType=").append(protocalType);
		sb.append('}');
		return sb.toString();
	}

	public byte[] getMsgBytes() {
		return msgBytes;
	}

	public void setMsgBytes(byte[] msgBytes) {
		this.msgBytes = msgBytes;
	}
}
