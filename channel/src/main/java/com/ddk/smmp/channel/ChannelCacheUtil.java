package com.ddk.smmp.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author leeson 2014-6-13 上午11:47:23 li_mr_ceo@163.com <br>
 * 
 */
public class ChannelCacheUtil {
	private static Map<String, Map<Object, Object>> channelCacheMap = new ConcurrentHashMap<String, Map<Object,Object>>();

	private synchronized static Map<Object, Object> _GetCache(String cache_name, boolean autoCreate) {
		Map<Object, Object> cache = null;
		if(channelCacheMap.containsKey(cache_name)){
			cache = channelCacheMap.get(cache_name);
		}else{
			if(autoCreate){
				channelCacheMap.put(cache_name, new ConcurrentHashMap<Object, Object>());
				cache = channelCacheMap.get(cache_name);
			}
		}
		return cache;
	}
	
	/**
	 * 
	 * 获取缓存中的数据
	 * 
	 * @param <T>
	 * 
	 * @param resultClass
	 * 
	 * @param name
	 * 
	 * @param key
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized static <T> T get(Class<T> resultClass, String name, Object key) {
		if (name != null && key != null) {
			Map<Object, Object> cache = _GetCache(name, true);
			Object element = cache.get(key);
			if (element != null) {
				T value = (T) element;
				return value;
			}
		}
		return null;
	}

	/**
	 * 
	 * 获取缓存中的数据
	 * 
	 * @param name
	 * 
	 * @param key
	 * 
	 * @return
	 */
	public synchronized static Object get(String name, Object key) {
		return get(Object.class, name, key);
	}

	/**
	 * 
	 * 写入缓存
	 * 
	 * @param name
	 * 
	 * @param key
	 * 
	 * @param value
	 */

	public synchronized static void put(String name, Object key, Object value) {
		if (name != null && key != null && value != null) {
			Map<Object, Object> cache = _GetCache(name, true);
			cache.put(key, value);
		}
	}

	/**
	 * 
	 * 清除缓冲中的某个数据
	 * 
	 * @param name
	 * 
	 * @param key
	 */
	public synchronized static void remove(String name, Object key) {
		if (name != null && key != null) {
			_GetCache(name, true).remove(key);
		}
	}

	public synchronized static void clear(String name) {
		_GetCache(name, true).clear();
	}
}