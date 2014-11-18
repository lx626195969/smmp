package com.sioo.cmppgw.util;

import java.util.Random;

/**
 * @author leeson 2014年8月22日 下午3:06:24 li_mr_ceo@163.com <br>
 * 
 */
public class PostKeyUtil {
	public static final String NUMBER_CHAR = "0123456789";

	public static synchronized String generateKey(long time) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random(time);
		for (int i = 0; i < 20; ++i)
			sb.append(NUMBER_CHAR.charAt(random.nextInt(NUMBER_CHAR.length())));

		return sb.toString();
	}

	public static synchronized boolean isEquals(long time, String key) {
		return generateKey(time).equals(key);
	}

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		System.out.println(time);

		String key = generateKey(time);
		System.out.println(key);

		System.out.println(isEquals(time, key));
	}
}
