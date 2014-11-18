package com.ddk.smmp.channel.cmpp._3.helper;

import java.util.ArrayList;
import java.util.List;

import com.ddk.smmp.channel.cmpp._3.utils.Tools;

/**
 * 
 * @author leeson 2014年8月11日 下午4:38:05 li_mr_ceo@163.com <br>
 *
 */
public class LongSMByte {
	
	/**
	 * 长短信拆分
	 * 
	 * @param channelSupportLen 通道支持最大短信字数
	 * @param signLen 需要预留的签名字数
	 * @param sm 短信内容
	 * @return
	 */
	public static List<byte[]> getLongByte(int channelSupportLen, int signLen, String sm) {
		List<byte[]> list = new ArrayList<byte[]>();

		try {
			byte[] messageUCS2;

			messageUCS2 = sm.getBytes("UnicodeBigUnmarked");

			int messageUCS2Len = messageUCS2.length;// 长短信长度(字节)
			int maxMessageLen = channelSupportLen * 2;// 支持最大短信长度(字节)
			int maxSignLen = signLen * 2;//签名占位长度(字节)
			boolean isAppend = false;//是否在结尾补发一条签名包
			
			int lastLen = 0;
			if (messageUCS2Len > (maxMessageLen - maxSignLen)) {// 长短信发送
				// int tpUdhi = 1;
				// 长消息是1.短消息是0
				// int msgFmt = 0x08;//长消息不能用GBK
				int messageUCS2Count = 0;//长短信分为多少条发送
				
				lastLen = messageUCS2Len % (maxMessageLen - 6);//最后一条短信的字数
				if(lastLen == 0){
					messageUCS2Count = messageUCS2Len / (maxMessageLen - 6);
					if(maxSignLen != 0){
						//说明是需要预留签名的通道，将拆分条数+1  并且倒数第二条短信要小于(maxMessageLen - 6)字节
						messageUCS2Count = messageUCS2Count + 1;
					}
				}else{
					messageUCS2Count = messageUCS2Len / (maxMessageLen - 6) + 1;
					
					//说明最后一条短信预留的签名位置不够
					if(lastLen > (maxMessageLen  - maxSignLen - 6)){
						isAppend = true;
						messageUCS2Count = messageUCS2Count + 1;
					}
				}
				
				byte[] tp_udhiHead = new byte[6];
				tp_udhiHead[0] = 0x05;
				tp_udhiHead[1] = 0x00;
				tp_udhiHead[2] = 0x03;
				tp_udhiHead[3] = (byte) Tools.generateSMS_NUM();
				tp_udhiHead[4] = (byte) messageUCS2Count;
				tp_udhiHead[5] = 0x01;// 默认为第一条
				for (int i = 0; i < messageUCS2Count; i++) {
					tp_udhiHead[5] = (byte) (i + 1);
					
					if(i == messageUCS2Count - 1){
						//最后一条
						if(isAppend){
							list.add(tp_udhiHead);
						}else{
							list.add(byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), messageUCS2Len));
						}
					}else{
						if(isAppend && i == messageUCS2Count - 2){
							list.add(byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), messageUCS2Len));
						}else{
							list.add(byteAdd(tp_udhiHead, messageUCS2, i * (maxMessageLen - 6), (i + 1) * (maxMessageLen - 6)));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private static byte[] byteAdd(byte[] tpUdhiHead, byte[] messageUCS2, int i, int j) {
		byte[] msgb = new byte[j - i + 6];
		System.arraycopy(tpUdhiHead, 0, msgb, 0, 6);
		System.arraycopy(messageUCS2, i, msgb, 6, j - i);
		return msgb;
	}
	
	public static void main(String[] args) throws Exception {
		StringBuffer str = new StringBuffer();
		for(int i = 0; i < 528; i++){
			str.append("李");
		}
		
		String tempStr = new String(str.toString().getBytes("UnicodeBigUnmarked"), "UnicodeBigUnmarked");
		System.out.println(tempStr.length() + ">" + tempStr);
		
		byte[] bytesTemp = new byte[str.toString().getBytes("UnicodeBigUnmarked").length];
		
		int index = 0;
		for(byte[] bytes : LongSMByte.getLongByte(70, 10, str.toString())){
			System.out.println(bytes.length);
			
			int copyLen = bytes.length - 6;
			System.arraycopy(bytes, 0 + 6, bytesTemp, index, copyLen);
			index = index + copyLen;
		}
		String finalStr = new String(bytesTemp, "UnicodeBigUnmarked");
		System.out.println(finalStr.length() + ">" + finalStr);
	}
}