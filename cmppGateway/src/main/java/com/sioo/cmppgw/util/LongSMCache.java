package com.sioo.cmppgw.util;

import java.util.Map;

/**
 * @author leeson 2014年8月22日 下午5:07:31 li_mr_ceo@163.com <br>
 * 
 */
public class LongSMCache {
	public static final String LONG_SM_CACHE = "long_sm_cache_key";
	
	/**
	 * 添加短消息片段 并返回当前所有片段
	 * 
	 * @param secquence 序列号
	 * @param msgId 消息ID
	 * @param uid 用户ID
	 * @param smSeq 长短信批标识
	 * @param content 短信片段
	 * @param length 总条数
	 * @param index 当前短信位置
	 * @return
	 */
	public static synchronized LongSM add(int secquence, String msgId, int uid, int smSeq, String content, int length, int index){
		LongSM sm = CacheUtil.get(LongSM.class, LONG_SM_CACHE, uid + "_" + smSeq);
		
		if(sm == null){
			sm = new LongSM(uid, smSeq);
			sm.setLength(length);
			sm.addContent(secquence, msgId, content, length, index);
			
			CacheUtil.put(LONG_SM_CACHE, uid + "_" + smSeq, sm);
		}else{
			sm.addContent(secquence, msgId, content, length, index);
		}
		
		return sm;
	}
	
	/**
	 * 移除5分钟前的长短信
	 */
	public static synchronized void removeInvalidSM(){
		Map<Object, Object> cache = CacheUtil._GetCache(LONG_SM_CACHE, false);
		if(null != cache){
			for(Object key : cache.keySet()){
				LongSM sm = (LongSM)cache.get(key.toString());
				if(sm.isValid()){
					CacheUtil.remove(LONG_SM_CACHE, key.toString());
				}
			}
		}
	}
}