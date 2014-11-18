package com.ddk.smmp.channel.sgip.msg.parent;

import org.apache.log4j.Logger;

/**
 * 
 * @author leeson 2014-6-9 下午02:18:54 li_mr_ceo@163.com
 *
 */
public class SmsObject {
	static protected Logger logger = Logger.getLogger(SmsObject.class.getName());

	static public Logger getLogger() {
		return logger;
	}

	static public void setLogger(Logger myLogger) {
		logger = myLogger;
	}
}