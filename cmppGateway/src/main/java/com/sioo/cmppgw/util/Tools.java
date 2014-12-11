package com.sioo.cmppgw.util;


/**
 * 
 * @author leeson 2014-6-9 下午06:06:32 li_mr_ceo@163.com <br>
 * 
 */
public class Tools {
	private static int sequence_Id = 0;
	private static long socket_Id = 0;
	private static int SMS_NUM = 0;
	/**
	 * 生成序列号
	 * 
	 * @return
	 */
	public synchronized static int generateSeq() {
		sequence_Id++;

		if (sequence_Id == Integer.MAX_VALUE) {
			sequence_Id = 0;
		}

		return sequence_Id;
	}
	
	/**
	 * 生成长短信批标识
	 * 
	 * @return
	 */
	public synchronized static int generateSMS_NUM() {
		if (SMS_NUM == 255) {
			SMS_NUM = 0;
		}
		
		SMS_NUM ++;
		
		return SMS_NUM;
	}
	
	public synchronized static long generateSocketID() {
		socket_Id++;

		if (socket_Id == Long.MAX_VALUE) {
			socket_Id = 0;
		}

		return socket_Id;
	}
}