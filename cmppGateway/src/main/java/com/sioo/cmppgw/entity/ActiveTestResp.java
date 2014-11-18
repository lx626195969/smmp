package com.sioo.cmppgw.entity;

import java.nio.ByteBuffer;

/**
 * 
 * @author leeson 2014年8月22日 上午9:18:37 li_mr_ceo@163.com <br>
 *
 */
public class ActiveTestResp extends CmppHead {
	private static final long serialVersionUID = 7120409294773243827L;
	private byte reserved;

	public ActiveTestResp() {

		commandId = CMPPConstant.APP_ACTIVE_TEST_RESP;
	}

	public byte getReserved() {
		return reserved;
	}

	public void setReserved(byte reserved) {
		this.reserved = reserved;
	}

	@Override
	protected void doSubEncode(ByteBuffer bb) {
		bb.put(reserved);
	}

	@Override
	protected void doSubDecode(ByteBuffer bb) {
		reserved = bb.get();
	}

	@Override
	protected void processHead() {
		totalLength = 13;
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

		ActiveTestResp that = (ActiveTestResp) o;

		return reserved == that.reserved;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (int) reserved;
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ActiveTestResp{");
		sb.append(super.toString());
		sb.append("reserved=").append(reserved);
		sb.append('}');
		return sb.toString();
	}
}
