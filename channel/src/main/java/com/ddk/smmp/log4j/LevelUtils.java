package com.ddk.smmp.log4j;

import org.apache.commons.lang.StringUtils;

/**
 * @author leeson 2014年10月28日 上午9:28:06 li_mr_ceo@163.com <br>
 * 
 */
public class LevelUtils {
	/**
	 * 获取一般日志的日志级别通过通道ID
	 * 
	 * @param cid
	 * @return
	 */
	public static int getSucLevel(int cid) {
		StringBuffer buffer = new StringBuffer("11");
		buffer.append(StringUtils.leftPad(cid + "", 3, "0"));
		return Integer.parseInt(buffer.toString());
	}

	/**
	 * 获取错误日志的日志级别通过通道ID
	 * 
	 * @param cid
	 * @return
	 */
	public static int getErrLevel(int cid) {
		StringBuffer buffer = new StringBuffer("12");
		buffer.append(StringUtils.leftPad(cid + "", 3, "0"));
		return Integer.parseInt(buffer.toString());
	}
}