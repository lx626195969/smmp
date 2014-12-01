package com.ddk.smmp.log4j;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SyslogAppender;

/**
 * @author leeson 2014年10月27日 下午3:29:41 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelLog {
	/**
	 * 
	 * @author leeson 2014年10月27日 下午3:30:08 li_mr_ceo@163.com <br>
	 */
	private static class ChannelLogLevel extends Level {
		private static final long serialVersionUID = -8541174063804131327L;

		protected ChannelLogLevel(int level, String levelStr, int syslogEquivalent) {
			super(level, levelStr, syslogEquivalent);
		}
	}

	/**
	 * 使用日志打印logger中的log方法
	 * 
	 * @param logger
	 * @param objLogInfo
	 *            日志信息
	 * @param levelInt
	 *            日志级别Int和log4j.xml中的对应上
	 */
	public static void log(Logger logger, Object objLogInfo, int levelInt) {
		logger.log(new ChannelLogLevel(levelInt, "SMS", SyslogAppender.LOG_LOCAL0), objLogInfo.toString() + "\r\n");
	}
	
	/**
	 * 使用日志打印logger中的log方法
	 * 
	 * @param logger
	 * @param objLogInfo
	 *            日志信息
	 * @param levelInt
	 *            日志级别Int和log4j.xml中的对应上
	 */
	public static void log(Logger logger, Object objLogInfo, int levelInt, Throwable throwable) {
		logger.log(new ChannelLogLevel(levelInt, "SMS", SyslogAppender.LOG_LOCAL0), objLogInfo.toString() + "\r\n", throwable);
	}
}