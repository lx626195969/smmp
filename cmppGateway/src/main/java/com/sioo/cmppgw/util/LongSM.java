package com.sioo.cmppgw.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author leeson 2014年8月22日 下午5:08:33 li_mr_ceo@163.com <br>
 * 
 */
public class LongSM {
	private int uid;
	private int smSeq;
	
	private long timestamp = System.currentTimeMillis();
	private int length = 0;
	private String[] contents = new String[0];
	private int[] secquences = new int[0];
	private String[] msgIds = new String[0];

	public LongSM(int uid, int smSeq) {
		super();
		this.uid = uid;
		this.smSeq = smSeq;
	}

	/**
	 * 往contents中添加 短消息片段
	 * 
	 * @param secquence 序列号
	 * @param content 短消息片段
	 * @param length 短消息总条数
	 * @param index 下标 从0开始
	 */
	public synchronized void addContent(int secquence, String msgId, String content, int length, int index){
		if(index > this.length - 1 || length != this.length || StringUtils.isNotEmpty(contents[index])){
			setLength(length);
		}
		contents[index] = content;
		msgIds[index] = msgId;
		secquences[index] = secquence;
	}
	
	/**
	 * 获取完整短信 如果不是完整的就返回NULL
	 * 
	 * @return
	 */
	public synchronized String getCompleteSM(){
		StringBuffer msg = new StringBuffer();
		
		for(String str : contents){
			if(StringUtils.isNotEmpty(str)){
				msg.append(str);
			}else{
				return null;
			}
		}
		
		CacheUtil.remove(LongSMCache.LONG_SM_CACHE, uid + "_" + smSeq);//清除缓存
		
		return msg.toString();
	}
	
	public String[] getMsgIds() {
		return msgIds;
	}

	public void setMsgIds(String[] msgIds) {
		this.msgIds = msgIds;
	}
	
	public int[] getSecquences() {
		return secquences;
	}

	public void setSecquences(int[] secquences) {
		this.secquences = secquences;
	}

	/**
	 * 是否过期短消息
	 * 
	 * @return
	 */
	public boolean isValid(){
		return (System.currentTimeMillis() - timestamp > 5 * 60 * 1000);
	}
	
	public int getLength() {
		return length;
	}

	public synchronized void setLength(int length) {
		this.length = length;
		this.contents = new String[length];
		this.msgIds = new String[length];
		this.secquences = new int[length];
	}
}