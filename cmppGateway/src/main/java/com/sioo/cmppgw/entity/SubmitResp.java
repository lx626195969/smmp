package com.sioo.cmppgw.entity;

import static java.lang.System.arraycopy;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 
 * @author leeson 2014年8月22日 上午9:21:06 li_mr_ceo@163.com <br>
 *
 */
public class SubmitResp extends CmppHead {
	private static final long serialVersionUID = 5793274928841536551L;
	
	private byte[] msgId = new byte[8];
	private int result;

	public SubmitResp(int protocalType) {
		super.protocalType = protocalType;
		super.commandId = CMPPConstant.APP_SUBMIT_RESP;
	}

	@Override
	protected void doSubEncode(ByteBuffer bb) {
		boolean isCmpp2 = protocalType == Constants.PROTOCALTYPE_VERSION_CMPP2;
		bb.put(msgId);
		if (isCmpp2) {
			bb.put((byte) result);
		} else {
			bb.putInt(result);
		}
	}

	@Override
	protected void doSubDecode(ByteBuffer bb) {
		boolean isCmpp2 = protocalType == Constants.PROTOCALTYPE_VERSION_CMPP2;
		bb.get(msgId);
		if (isCmpp2) {
			result = bb.get();
		} else {
			result = bb.getInt();
		}
	}

	@Override
	protected void processHead() {
		boolean isCmpp2 = protocalType == Constants.PROTOCALTYPE_VERSION_CMPP2;
		totalLength = isCmpp2 ? 21 : 24;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		SubmitResp that = (SubmitResp) o;

		if (result != that.result) {
			return false;
		}
		if (!Arrays.equals(msgId, that.msgId)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result1 = super.hashCode();
		result1 = 31 * result1 + (msgId != null ? Arrays.hashCode(msgId) : 0);
		result1 = 31 * result1 + result;
		return result1;
	}

	public byte[] getMsgId() {
		return msgId;
	}

	public void setMsgId(byte[] msgId) {
		this.msgId = msgId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SubmitResp{");
		sb.append(super.toString());
		sb.append("msgId=");
		if (msgId == null) {
			sb.append("null");
		} else {
			sb.append('[');
			for (int i = 0; i < msgId.length; ++i) {
				sb.append(i == 0 ? "" : ", ").append(msgId[i]);
			}
			sb.append(']');
		}
		sb.append(", result=").append(result);
		sb.append('}');
		return sb.toString();
	}

	@Override
	public SubmitResp clone()  {
		SubmitResp submitResp = new SubmitResp(this.protocalType);
		submitResp.totalLength = this.totalLength;
		submitResp.commandId = this.commandId;
		submitResp.secquenceId = this.secquenceId;
		
        arraycopy(this.msgId, 0, submitResp.msgId, 0, 8);
        submitResp.setResult(this.result);
        return submitResp;
	}
}
