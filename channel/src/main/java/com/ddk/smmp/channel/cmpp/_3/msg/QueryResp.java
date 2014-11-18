package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午09:01:46 li_mr_ceo@163.com <br>
 * 
 */
public class QueryResp extends Response {

	/** 时间(精确至日) */
	private String time = "";
	/**
	 * 查询类别 0：总数查询 1：按业务类型查询
	 */
	private byte queryType = 0x00;
	/** 查询码 */
	private String queryCode = "";
	/** 从SP接收信息总数 */
	private int mt_tlmsg = 0;
	/** 从SP接收用户总数 */
	private int mt_tlusr = 0;
	/** 成功转发数量 */
	private int mt_scs = 0;
	/** 待转发数量 */
	private int mt_wt = 0;
	/** 转发失败数量 */
	private int mt_fl = 0;
	/** 向SP成功送达数量 */
	private int mo_scs = 0;
	/** 向SP待送达数量 */
	private int mo_wt = 0;
	/** 向SP送达失败数量 */
	private int mo_fl = 0;

	public int getMo_fl() {
		return mo_fl;
	}

	public void setMo_fl(int mo_fl) {
		this.mo_fl = mo_fl;
	}

	public int getMo_scs() {
		return mo_scs;
	}

	public void setMo_scs(int mo_scs) {
		this.mo_scs = mo_scs;
	}

	public int getMo_wt() {
		return mo_wt;
	}

	public void setMo_wt(int mo_wt) {
		this.mo_wt = mo_wt;
	}

	public int getMt_fl() {
		return mt_fl;
	}

	public void setMt_fl(int mt_fl) {
		this.mt_fl = mt_fl;
	}

	public int getMt_scs() {
		return mt_scs;
	}

	public void setMt_scs(int mt_scs) {
		this.mt_scs = mt_scs;
	}

	public int getMt_tlmsg() {
		return mt_tlmsg;
	}

	public void setMt_tlmsg(int mt_tlmsg) {
		this.mt_tlmsg = mt_tlmsg;
	}

	public int getMt_tlusr() {
		return mt_tlusr;
	}

	public void setMt_tlusr(int mt_tlusr) {
		this.mt_tlusr = mt_tlusr;
	}

	public int getMt_wt() {
		return mt_wt;
	}

	public void setMt_wt(int mt_wt) {
		this.mt_wt = mt_wt;
	}

	public String getQueryCode() {
		return queryCode;
	}

	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}

	public byte getQueryType() {
		return queryType;
	}

	public void setQueryType(byte queryType) {
		this.queryType = queryType;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public QueryResp() {
		super(CmppConstant.CMD_QUERY_RESP);
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

	protected ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(getTime(), 8);
		buffer.appendByte(getQueryType());
		buffer.appendString(getQueryCode(), 10);
		buffer.appendInt(getMt_tlmsg());
		buffer.appendInt(getMt_tlusr());
		buffer.appendInt(getMt_scs());
		buffer.appendInt(getMt_wt());
		buffer.appendInt(getMt_fl());
		buffer.appendInt(getMo_scs());
		buffer.appendInt(getMo_wt());
		buffer.appendInt(getMo_fl());
		return buffer;
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			setTime(buffer.removeStringEx(8));
			setQueryType(buffer.removeByte());
			setQueryCode(buffer.removeStringEx(10));
			setMt_tlmsg(buffer.removeInt());
			setMt_tlusr(buffer.removeInt());
			setMt_scs(buffer.removeInt());
			setMt_wt(buffer.removeInt());
			setMt_fl(buffer.removeInt());
			setMo_scs(buffer.removeInt());
			setMo_wt(buffer.removeInt());
			setMo_fl(buffer.removeInt());
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	@Override
	public String name() {
		return "CMPP QueryResp";
	}
}