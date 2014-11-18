package com.sioo.cmppgw.jdbc.lang;

/**
 * 字符串工具
 */
public final class StringTools {

	/**
	 * 拼接结果
	 * 
	 * @param args
	 * @return
	 */
	public static String splice(Object... args) {
		StringBuilder sb = new StringBuilder();
		for (Object arg : args) {
			sb.append(arg);
		}
		return sb.toString();
	}

}
