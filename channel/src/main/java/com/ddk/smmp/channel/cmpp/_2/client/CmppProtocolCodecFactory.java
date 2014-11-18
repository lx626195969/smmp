package com.ddk.smmp.channel.cmpp._2.client;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
 * @author leeson 2014-6-10 上午11:22:03 li_mr_ceo@163.com <br>
 *         消息CODEC Factory
 */
public class CmppProtocolCodecFactory implements ProtocolCodecFactory {
	int cid;
	
	public CmppProtocolCodecFactory(int cid) {
		super();
		this.cid = cid;
	}
	
	public CmppProtocolCodecFactory() {
		super();
	}

	public ProtocolDecoder getDecoder(IoSession sessionIn) throws Exception {
		return new CmppRequestDecoder(cid);
	}

	public ProtocolEncoder getEncoder(IoSession sessionIn) throws Exception {
		return new CmppResponseEncoder(cid);
	}
}
