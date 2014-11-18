package com.ddk.smmp.adapter.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 
 * @author leeson 2014年7月7日 下午3:53:53 li_mr_ceo@163.com <br>
 *
 */
public class CacheUtil {
	private static Map<String, Map<Object, Object>> cacheMap = new ConcurrentHashMap<String, Map<Object,Object>>();

	public static void main(String[] args){
		System.out.println(DigestUtils.md5Hex("nimda"));
	}

	private synchronized static Map<Object, Object> _GetCache(String cache_name, boolean autoCreate) {
		Map<Object, Object> cache = null;
		if(cacheMap.containsKey(cache_name)){
			cache = cacheMap.get(cache_name);
		}else{
			if(autoCreate){
				cacheMap.put(cache_name, new ConcurrentHashMap<Object, Object>());
				cache = cacheMap.get(cache_name);
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