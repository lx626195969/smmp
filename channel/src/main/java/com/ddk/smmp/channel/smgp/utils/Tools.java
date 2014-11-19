package com.ddk.smmp.channel.smgp.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 
 * @author leeson 2014-6-9 下午06:06:32 li_mr_ceo@163.com <br>
 * 
 */
public class Tools {
	private static int sequence_Id = 0;
	private static int SMS_NUM = 0;

	/**
	 * 解析 SMGP 提交响应和报告的 MSGID
	 * 
	 * @param msgId
	 * @return
	 */
	public static String resolveSMGP_MsgId(byte[] msgId) {
		if (msgId.length != 10) {
			return null;
		}
//		return BCDUtils.bcd2Str(Arrays.copyOfRange(msgId, 0, 3))
//				+ BCDUtils.bcd2Str(Arrays.copyOfRange(msgId, 3, 7))
//				+ BCDUtils.bcd2Str(Arrays.copyOfRange(msgId, 7, 10));
		//由于太长转不了Long类型 所以去掉第一部分
		return BCDUtils.bcd2Str(Arrays.copyOfRange(msgId, 3, 7))
				+ BCDUtils.bcd2Str(Arrays.copyOfRange(msgId, 7, 10));
	}
	
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

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(0, x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, 0, bytes.length);
		buffer.flip();
		return buffer.getLong();
	}
}