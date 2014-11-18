package com.sioo.cmppgw.socket;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leeson 2014年9月10日 下午4:13:37 li_mr_ceo@163.com <br>
 * 
 */
public class SmsTransferClientHandler extends IoHandlerAdapter {
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

	@Override
	public void sessionOpened(IoSession session) {

	}

	@Override
	public void messageReceived(IoSession session, Object message) {
		logger.info("R <- " + message);
		session.setAttribute("result", message.toString());
		session.close(true);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		logger.error(cause.getMessage());
		session.close(true);
	}
}