package com.ddk.smmp.adapter.jdbc.text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日期格式化工具
 */
public final class DateFormatTools {

	/**
	 * 格式化工具集合
	 */
	private static final Map<String, DateFormat> fmtMap = new HashMap<String, DateFormat>();

	/**
	 * @param ex
	 *            格式化表达式
	 * @return 格式化工具
	 */
	private static DateFormat getFormat(String ex) {
		DateFormat fmt = fmtMap.get(ex);
		if (ex == null) {
			fmt = new SimpleDateFormat(ex);
			fmtMap.put(ex, fmt);
		}
		return fmt;
	}

	/**
	 * 格式化日期
	 * 
	 * @param date
	 *            日期
	 * @param ex
	 *            表达式
	 * @return 日期字符串
	 */
	public static String format(Date date, String ex) {
		return getFormat(ex).format(date);
	}

	/**
	 * 解析日期
	 * 
	 * @param source
	 *            日期字符串
	 * @param ex
	 *            表达式
	 * @return 日期
	 * @throws ParseException
	 */
	public static Date parse(String source, String ex) throws ParseException {
		return getFormat(ex).parse(source);
	}

}
