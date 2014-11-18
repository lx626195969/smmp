package com.ddk.smmp.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @author lixin li_mr_ceo@163.com
 * @version 2013-5-9 下午01:42:10
 * @desc
 */
public class DateUtils {
	private static String defaultDatePattern = "yyyy-MM-dd";

	/**
	 * 获得当前日期
	 * 
	 * @return
	 */
	public static Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	/**
	 * 获得默认的 date pattern
	 */
	public static String getDatePattern() {
		return defaultDatePattern;
	}

	/**
	 * 返回预设Format的当前日期字符串
	 */
	public static String getToday() {
		Date today = new Date();
		return format(today);
	}

	/**
	 * 根据传入的模式参数返回当天的日期
	 * 
	 * @param pattern
	 *            传入的模式
	 * @return 按传入的模式返回一个字符串
	 */
	public static String getToday(String pattern) {
		if (!StringUtils.isNotEmpty(pattern))
			return getToday();
		Date date = new Date();
		return format(date, pattern);
	}

	/**
	 * 使用预设Format格式化Date成字符串
	 */
	public static String format(Date date) {
		return date == null ? "" : format(date, getDatePattern());
	}

	/**
	 * 使用参数Format格式化Date成字符串
	 */
	public static String format(Date date, String pattern) {
		return date == null ? "" : new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 使用预设格式将字符串转为Date
	 */
	public static Date parse(String strDate) {
		return !StringUtils.isNotEmpty(strDate) ? null : parse(strDate,
				getDatePattern());
	}

	/**
	 * 使用参数Format将字符串转为Date
	 */
	public static Date parse(String strDate, String pattern) {
		try {
			return !StringUtils.isNotEmpty(strDate) ? null
					: new SimpleDateFormat(pattern).parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 在日期上增加数个整月
	 */
	public static Date addMonth(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, n);
		return cal.getTime();
	}

	/**
	 * 在日期上增加数个小时
	 */
	public static Date addHOUR(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, n);
		return cal.getTime();
	}

	/**
	 * 在日期上增加数分钟
	 */
	public static Date addMinutes(Date date, int n) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, n);
		return cal.getTime();
	}

	/**
	 * 获取某时间的中文星期（如：星期一、星期二），每星期的第一天是星期日
	 * 
	 * @param date
	 *            ：日期
	 * @return
	 */
	public static String getWeekCS(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String[] week = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return week[calendar.get(Calendar.DAY_OF_WEEK) - 1];
	}

	/**
	 * 获取当前时间的中文星期（如：星期一、星期二），每星期的第一天是星期日
	 * 
	 * @return
	 */
	public static String getWeekCSToday() {
		return getWeekCS(new Date());
	}

	/**
	 * 用当前日期作为文件名，一般不会重名取到的值是从当前时间的字符串格式，带有微秒，建议作为记录id
	 * 
	 * @return
	 */
	public static String getTimeStamp(String strFormat) {
		Date currentTime = new Date();
		return dateToString(currentTime, strFormat);
	}

	/**
	 * 用当前日期作为文件名，一般不会重名取到的值是从1970年1月1日00:00:00开始算起所经过的微秒数
	 * 
	 * @return
	 */
	public static String getFileName() {
		Calendar calendar = Calendar.getInstance();
		String filename = String.valueOf(calendar.getTimeInMillis());
		return filename;
	}

	/**
	 * 获取两个日期之间所差的天数
	 * 
	 * @param from
	 *            ：开始日期
	 * @param to
	 *            ：结束日期
	 * @return：所差的天数(非负整数)
	 */
	public static int dateNum(Date from, Date to) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(from);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date fromDate = calendar.getTime();

		calendar.setTime(to);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date toDate = calendar.getTime();
		int diff = Math
				.abs((int) ((fromDate.getTime() - toDate.getTime()) / (24 * 3600 * 1000)));
		return diff;
	}

	/**
	 * 获取两个日期之间所差的周数
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static int weekNum(Date from, Date to) {

		return 0;
	}

	/**
	 * 获取date前或后nDay天的日期
	 * 
	 * @param date
	 *            ：开始日期
	 * @param nDay
	 *            ：天数
	 * @param type
	 *            ：正为后nDay天的日期，否则为前nDay天的日期。
	 * @return
	 */
	private static Date getDate(Date date, int nDay, int type) {
		long millisecond = date.getTime(); // 从1970年1月1日00:00:00开始算起所经过的微秒数
		long msel = nDay * 24 * 3600 * 1000l; // 获取nDay天总的毫秒数
		millisecond = millisecond + ((type > 0) ? msel : (-msel));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millisecond);
		return calendar.getTime();
	}

	/**
	 * 获取n天后的日期
	 * 
	 * @param date
	 * @param nDay
	 * @return
	 */
	public static Date dateAfterNDate(Date date, int nDay) {
		return getDate(date, nDay, 1);
	}

	/**
	 * 获取n天后的日期
	 * 
	 * @param strDate
	 * @param nDay
	 * @return
	 */
	public static Date dateAfterNDate(String strDate, int nDay) {
		Date date = stringToDate(strDate, "yyyy-MM-dd HH:mm:ss");
		return dateAfterNDate(date, nDay);
	}

	/**
	 * 获取n天前的日期
	 * 
	 * @param date
	 * @param nDay
	 * @return
	 */
	public static Date dateBeforeNDate(Date date, int nDay) {
		return getDate(date, nDay, -1);
	}

	/**
	 * 获取n天前的日期
	 * 
	 * @param strDate
	 * @param nDay
	 * @return
	 */
	public static Date dateBeforeNDate(String strDate, int nDay) {
		Date date = stringToDate(strDate, "yyyy-MM-dd HH:mm:ss");
		return dateBeforeNDate(date, nDay);
	}

	/**
	 * 将日期转化为字符串的形式
	 * 
	 * @param date
	 * @param strFormat
	 * @return
	 */
	public static String dateToString(Date date, String strFormat) {
		if (strFormat == null) {
			strFormat = "yyyy-MM-dd";
		}
		SimpleDateFormat format = new SimpleDateFormat(strFormat);
		if (date != null)
			return format.format(date);
		else
			return null;
	}

	/**
	 * 将字符串转化为Date类型。如果该字符串无法转化为Date类型的数据，则返回null。
	 * 
	 * @param strDate
	 * @param strFormat
	 *            strDate和strFormat的格式要一样。即如果strDate="20061112"，则strFormat="yyyyMMdd"
	 * @return
	 */
	public static Date stringToDate(String strDate, String strFormat) {
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
			date = sdf.parse(strDate);
			if (!sdf.format(date).equals(strDate)) {
				date = null;
			}
		} catch (Exception e) {
			date = null;
		}
		return date;
	}

	/**
	 * 获取n月之前或之后的日期
	 * 
	 * @param date
	 * @param nMonth
	 * @param type
	 *            (只能为-1或1)
	 * @return
	 */
	public static Date getDateMonth(Date date, int nMonth, int type) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int nYear = nMonth / 12;
		int month = nMonth % 12;
		calendar.add(Calendar.YEAR, nYear * type);
		Date desDate = calendar.getTime();
		calendar.add(Calendar.MONTH, month * type);
		if (type < 0) {
			while (!calendar.getTime().before(desDate)) {
				calendar.add(Calendar.YEAR, type);
			}
		} else {
			while (!calendar.getTime().after(desDate)) {
				calendar.add(Calendar.YEAR, type);
			}
		}
		return calendar.getTime();
	}

	/**
	 * 获取当前时间所在的周的最后一天（周日为第一天）
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_WEEK);
		date = DateUtils.dateAfterNDate(date, 7 - index);
		return date;
	}

	/**
	 * 获取当前时间所在的周的第一天（周日为第一天）
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.DAY_OF_WEEK);
		date = DateUtils.dateBeforeNDate(date, index - 1);
		return date;
	}

	/**
	 * 获取date所在的月份的最后一天 方法是获取下个月的第一天，然后获取前一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfMonth(Date date) {
		date = getFirstDateOfNextMonth(date);
		date = dateBeforeNDate(date, 1);
		return date;
	}

	/**
	 * 获取所在月的下个月的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfNextMonth(Date date) {
		date = getLastDateOfMonth(date);
		return getDateMonth(date, 1, 1);
	}

	/**
	 * 获取当月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}

	/**
	 * 获取下个月的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfNextMonth(Date date) {
		date = getFirstDateOfMonth(date);
		return getDateMonth(date, 1, 1);
	}

	/**
	 * 获取上个月第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDateOfBeforeMonth(Date date) {
		date = getFirstDateOfMonth(date);
		return getDateMonth(date, -1, 1);
	}

	/**
	 * 获取季度的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDateOfSeason(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int index = calendar.get(Calendar.MONTH);
		index = index / 3;
		Date[] dates = new Date[4];
		calendar.set(calendar.get(Calendar.YEAR), 2, 31);
		dates[0] = calendar.getTime();
		calendar.set(calendar.get(Calendar.YEAR), 5, 30);
		dates[1] = calendar.getTime();
		calendar.set(calendar.get(Calendar.YEAR), 8, 30);
		dates[2] = calendar.getTime();
		calendar.set(calendar.get(Calendar.YEAR), 11, 31);
		dates[3] = calendar.getTime();
		return dates[index];
	}

	/**
	 * 创建日期date
	 * 
	 * @param year
	 *            ：年
	 * @param month
	 *            ：月
	 * @param day
	 *            ：日
	 * @return
	 */
	public static Date createDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		return calendar.getTime();
	}

	public static Date getDateByPattern(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date curDate = null;
		try {
			curDate = format.parse(format.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return curDate;
	}

	public static Date getBeginDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}

	public static Date getToDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime();
	}

	public static Date stringToDate(String time) {
		SimpleDateFormat formatter;
		int tempPos = time.indexOf("AD");
		time = time.trim();
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		if (tempPos > -1) {
			time = time.substring(0, tempPos) + "公元"
					+ time.substring(tempPos + "AD".length());// china
			formatter = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");
		}
		tempPos = time.indexOf("-");
		if ((time.indexOf("年") > -1)) {
			if (time.split(":").length == 3) {
				formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			} else if (time.split(":").length == 2) {
				formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
			} else
				formatter = new SimpleDateFormat("yyyy年MM月dd日");
		} else if ((time.indexOf("/") > -1) && (time.indexOf("am") > -1)
				|| (time.indexOf("pm") > -1)
				|| (time.indexOf("AM") > -1 || (time.indexOf("PM") > -1))) {
			formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			java.util.Date ctime = formatter.parse(time, pos);
			if (time.indexOf("PM") > -1) {
				ctime.setTime((ctime.getTime() + 12 * 60 * 60 * 1000));
			}
			return ctime;

		} else if ((time.indexOf("/") > -1) && (time.indexOf(" ") > -1)) {
			if (time.split(":").length == 3) {
				formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			} else if (time.split(":").length == 2) {
				formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			} else
				formatter = new SimpleDateFormat("yyyy/MM/dd");
		} else if ((time.indexOf("-") > -1) && (time.indexOf(" ") > -1)) {
			if (time.split(":").length == 3) {
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			} else if (time.split(":").length == 2) {
				formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			} else
				formatter = new SimpleDateFormat("yyyy-MM-dd");
		}
		ParsePosition pos = new ParsePosition(0);
		java.util.Date ctime = formatter.parse(time, pos);

		return ctime;
	}

	public static Timestamp getSqlTimestamp(Date dateValue) {
		try {
			if ((dateValue == null) || (dateValue.equals("")))
				return null;
			Timestamp newDate = new Timestamp(dateValue.getTime());
			return newDate;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 格式化一个日期的时分秒为00:00:00
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getDate000000(Date date) {
		Date result = null;
		if (null != date) {
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);

			result = new Date(date.getTime());
		}
		return result;
	}

	/**
	 * 格式化一个日期的时分秒为23:59:59
	 * 
	 * @param date
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getDate235959(Date date) {
		Date result = null;
		if (null != date) {
			date.setHours(23);
			date.setMinutes(59);
			date.setSeconds(59);

			result = new Date(date.getTime());
		}
		return result;
	}

	public static void main(String[] agrs) {
		System.out.println(getFirstDateOfWeek(parse("2013-07-13 23:30:00", "yyyy-MM-dd HH:mm:ss")));
		System.out.println(getLastDateOfWeek(parse("2013-07-13 23:30:00", "yyyy-MM-dd HH:mm:ss")));
	}
}