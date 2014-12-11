package com.ddk.smmp.adapter.utils;

/**
 * @author leeson 2014年12月11日 下午3:35:44 li_mr_ceo@163.com <br>
 * 
 */
public class Tools {
	private static long socket_Id = 0;
	public synchronized static long generateSocketID() {
		socket_Id++;

		if (socket_Id == Long.MAX_VALUE) {
			socket_Id = 0;
		}

		return socket_Id;
	}
}
