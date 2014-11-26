package com.ddk.smmp.channel.liancheng_http.utils;

/**
 * 
 * @author leeson 2014年10月22日 上午11:21:44 li_mr_ceo@163.com <br>
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