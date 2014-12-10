package com.ddk.smmp.channel.dingyuan_http.utils;


/**
 * 
 * @author leeson 2014-6-9 下午06:06:32 li_mr_ceo@163.com <br>
 * 
 */
public class Tools {
	private static int sequence_Id = 0;

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
}