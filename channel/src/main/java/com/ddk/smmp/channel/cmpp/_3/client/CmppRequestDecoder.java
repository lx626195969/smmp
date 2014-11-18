package com.ddk.smmp.channel.cmpp._3.client;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.log4j.Logger;

import com.ddk.smmp.channel.cmpp._3.helper.ByteBuffer;
import com.ddk.smmp.channel.cmpp._3.helper.CmppMSGParser;
import com.ddk.smmp.channel.cmpp._3.msg.parent.CmppMSG;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014-6-10 上午10:58:24 li_mr_ceo@163.com <br>
 *         消息解码
 */
public class CmppRequestDecoder extends CumulativeProtocolDecoder {
	private static final Logger logger = Logger.getLogger(CmppRequestDecoder.class);
	
	int cid;
	
	public CmppRequestDecoder() {
		super();
	}

	public CmppRequestDecoder(int cid) {
		super();
		this.cid = cid;
	}


	@Override
	protected boolean doDecode(final IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// 考虑以下几种情况：
		// 1. 一个ip包中只包含一个完整消息
		// 2. 一个ip包中包含一个完整消息和另一个消息的一部分
		// 3. 一个ip包中包含一个消息的一部分
		// 4. 一个ip包中包含两个完整的数据消息或更多（循环处理在父类的decode中）
		if (in.remaining() > 4) {
			in.mark();
			
			int length = in.getInt();
			if (length > (in.remaining() + 4))
			{
				in.reset();
				return false;
			}

			byte[] bytedata = new byte[length - 4];
			in.get(bytedata);
			ByteBuffer buffer = new ByteBuffer();
			buffer.appendInt(length);
			buffer.appendBytes(bytedata);
			
			CmppMSG msg = CmppMSGParser.createMSGFromBuffer(buffer);
			
			if (msg == null) return false;
			
			out.write(msg);
			
			ChannelLog.log(
					logger,
					"resv msg " + in.toString() + "\r\n" + "commandLength<"
							+ msg.getCommandLength() + "> commandId<"
							+ msg.getCommandId() + "> seq<"
							+ msg.getSequenceNumber() + ">" + msg.dump(),
					LevelUtils.getSucLevel(cid));

			return true;

		}
		return false;
	}
}