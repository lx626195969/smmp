package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;
import com.ddk.smmp.channel.cmpp._3.utils.Tools;

/**
 * 
 * @author leeson 2014-6-10 上午09:12:11 li_mr_ceo@163.com <br>
 * 
 */
public class DeliverResp extends Response {

	/**
	 * 信息标识 （CMPP_DELIVER中的Msg_Id字段）
	 */
	private long msgId = 0l;

	/**
	 * 结果 0：正确 1：消息结构错 2：命令字错 3：消息序号重复 4：消息长度错 <br>
	 * 5：资费代码错 6：超过最大信息长 7：业务代码错 8: 流量控制错 9 ：其他错误
	 */
	private int result = 0;

	public DeliverResp() {
		super(CmppConstant.CMD_DELIVER_RESP);
	}

	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = Tools.bytesToLong(buffer.removeBytes(8).getBuffer());
			result = buffer.removeInt();
		} catch (NotEnoughDataInByteBufferException e) {
			e.printStackTrace();
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendBytes(Tools.longToBytes(msgId));
		buffer.appendInt(result);
		return buffer;
	}

	public long getMsgId() {
		return msgId;
	}

	public void setMsgId(long msgId) {
		this.msgId = msgId;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String name() {
		return "CMPP DeliverResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nDeliverResp*****************************"
				  + "\r\nmsgId:     " + msgId
				  + "\r\nresult:    " + result
				  + "\r\n*****************************DeliverResp";
		return rt;
	}
}