package com.ddk.smmp.channel.cmpp._3.msg;

import com.ddk.smmp.channel.cmpp._3.exception.MSGException;
import com.ddk.smmp.channel.cmpp._3.exception.NotEnoughDataInByteBufferException;
import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppConstant;
import com.ddk.smmp.channel.cmpp._3.msg.parent.Response;
import com.ddk.smmp.channel.cmpp._3.utils.Tools;

/**
 * 
 * @author leeson 2014-6-9 下午06:13:14 li_mr_ceo@163.com <br>
 * 
 */
public class SubmitResp extends Response {
	/**
	 * 信息标识，生成算法如下： 采用64位（8字节）的整数： 时间（格式为MMDDHHMMSS，即月日时分秒）：bit64~bit39，其中
	 * bit64~bit61：月份的二进制表示； bit60~bit56：日的二进制表示； bit55~bit51：小时的二进制表示；
	 * bit50~bit45：分的二进制表示； bit44~bit39：秒的二进制表示；
	 * 短信网关代码：bit38~bit17，把短信网关的代码转换为整数填写到该字段中； 序列号：bit16~bit1，顺序增加，步长为1，循环使用。
	 * 各部分如不能填满，左补零，右对齐。 （SP根据请求和应答消息的Sequence_Id一致性就可得到CMPP_Submit消息的Msg_Id）
	 */
	private long msgId = 0l;
	/**
	 * 结果： 0：正确； 1：消息结构错； 2：命令字错； 3：消息序号重复； 4：消息长度错； 5：资费错； 6：超过最大信息长； 7：业务代码错；
	 * 8：流量控制错； 　9：本网关不负责服务此计费号码； 　10：Src_Id错误； 　11：Msg_src错误；
	 *　12：Fee_terminal_Id错误； 　13：Dest_terminal_Id错误；
	 */
	private int result = 0;

	public SubmitResp() {
		super(CmppConstant.CMD_SUBMIT_RESP);
	}

	@Override
	public ByteBuffer getData() {
		ByteBuffer bodyBuf = getBody();
		header.setCommandLength(CmppConstant.PDU_HEADER_SIZE + bodyBuf.length());
		ByteBuffer buffer = header.getData();
		buffer.appendBuffer(bodyBuf);
		return buffer;
	}

	@Override
	public void setData(ByteBuffer buffer) throws MSGException {
		header.setData(buffer);
		setBody(buffer);
	}

	public void setBody(ByteBuffer buffer) throws MSGException {
		try {
			msgId = Tools.bytesToLong(buffer.removeBytes(8).getBuffer());
			result = buffer.removeInt();
		} catch (NotEnoughDataInByteBufferException e) {
			throw new MSGException(e);
		}
	}

	public ByteBuffer getBody() {
		ByteBuffer buffer = new ByteBuffer();
		buffer.appendBytes(Tools.longToBytes(msgId), 8);
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
		return "CMPP SubmitResp";
	}

	@Override
	public String dump() {
		String rt = "\r\nSubmitResp********************************"
				  + "\r\nmsgId:      " + msgId
				  + "\r\nresult:     " + result
				  + "\r\n********************************SubmitResp";
		return rt;
	}
}