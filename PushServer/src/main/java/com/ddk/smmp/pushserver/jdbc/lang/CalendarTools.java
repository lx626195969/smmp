package com.ddk.smmp.pushserver.jdbc.lang;

import java.util.Calendar;
import java.util.Date;

/**
 * 日历工具
 */
public class CalendarTools {

	/**
	 * @param date
	 *            日期
	 * @return 日历
	 */
	public static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @param day
	 *            当月第几天
	 * @return 日历
	 */
	public static Calendar getCalendar(int year, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day);
		return cal;
	}

}
