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
	private ProtocolDecoder decoder = new SmgpRequestDecoder();
	private ProtocolEncoder encoder = new SmgpResponseEncoder();

	public ProtocolDecoder getDecoder(IoSession sessionIn) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession sessionIn) throws Exception {
		return encoder;
	}
}
