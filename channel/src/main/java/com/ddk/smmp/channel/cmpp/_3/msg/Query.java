package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Request;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;

/**
 * 
 * @author leeson 2014-6-10 上午08:57:58 li_mr_ceo@163.com <br>
 *         CMPP_QUERY操作的目的是SP向ISMG查询某时间的业务统计情况，可以按总数或按业务代码查询。ISMG以CMPP_QUERY_RESP应答
 */
public class Query extends Request {

	/** 时间YYYYMMDD(精确至日) */
	private String time = "";

	/**
	 * 查询类别： 0：总数查询； 1：按业务类型查询。
	 */
	private byte queryType = 0x00;

	/**
	 * 查询码。 当Query_Type为0时，此项无效；当Query_Type为1时，此项填写业务类型Service_Id.。
	 */
	private String queryCode = "";

	/** 保留。 */
	private String reserve = "";
	
	public Query() {
		super(CmppConstant.CMD_QUERY);
	}

	@Override
	protected Response createResponse() {
		return new QueryResp();
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

	private ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendString(time, 8);
		buffer.appendByte(queryType);
		buffer.appendString(queryCode, 10);
		buffer.appendString(reserve, 8);
		return buffer;
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			time = buffer.removeStringEx(8);
			queryType = buffer.removeByte();
			queryCode = buffer.removeStringEx(10);
			reserve = buffer.removeStringEx(8);
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public String getQueryCode() {
		return queryCode;
	}

	public void setQueryCode(String queryCode) {
		this.queryCode = queryCode;
	}

	public int getQueryType() {
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

	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}

	@Override
	public String name() {
		return "CMPP Query";
	}
}