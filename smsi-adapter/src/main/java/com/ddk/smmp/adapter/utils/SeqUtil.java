package com.ddk.smmp.adapter.utils;

/**
 * @author leeson 2014年7月9日 下午4:11:46 li_mr_ceo@163.com <br>
 * 
 */
public class SeqUtil {
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