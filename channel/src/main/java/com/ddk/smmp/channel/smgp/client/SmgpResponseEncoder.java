package com.ddk.smmp.channel.smgp.client;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.ddk.smmp.channel.smgp.msg.parent.SmgpMSG;
import com.ddk.smmp.log4j.ChannelLog;
import com.ddk.smmp.log4j.LevelUtils;

/**
 * 
 * @author leeson 2014-6-10 上午10:59:16 li_mr_ceo@163.com <br>
 *         消息编码
 */
public class SmgpResponseEncoder extends ProtocolEncoderAdapter {
	private static final Logger logger = Logger.getLogger(SmgpResponseEncoder.class);

	private int cid;
	
	public SmgpResponseEncoder() {
		super();
	}
	
	public SmgpResponseEncoder(int cid) {
		super();
		this.cid = cid;
	}
	
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		SmgpMSG msg = (SmgpMSG) message;
		byte[] bytes = msg.getData().getBuffer();
		
		IoBuffer buf = IoBuffer.allocate(bytes.length, false);
		buf.setAutoExpand(true);
		buf.put(bytes);
		buf.flip();
		
		ChannelLog.log(logger, "send msg " + buf.toString() + "\r\n" + "commandLength<" + msg.getCommandLength() + "> commandId<" + msg.getCommandId() + "> seq<" + msg.getSequenceNumber() + ">" + msg.dump(), LevelUtils.getSucLevel(cid));
		
		out.write(buf);
	}
}