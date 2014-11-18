package com.ddk.smmp.channel.smgp.client;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
 * @author leeson 2014-6-10 上午11:22:03 li_mr_ceo@163.com <br>
 *         消息CODEC Factory
 */
public class SmgpProtocolCodecFactory implements ProtocolCodecFactory {
	
	int cid;
	
	public SmgpProtocolCodecFactory(int cid) {
		super();
		this.cid = cid;
	}
	
	public ProtocolDecoder getDecoder(IoSession sessionIn) throws Exception {
		return new SmgpRequestDecoder(cid);
	}

	public ProtocolEncoder getEncoder(IoSession sessionIn) throws Exception {
		return new SmgpResponseEncoder(cid);
	}
}
