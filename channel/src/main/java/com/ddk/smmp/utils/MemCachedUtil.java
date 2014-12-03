package com.ddk.smmp.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.alisoft.xplatform.asf.cache.ICacheManager;
import com.alisoft.xplatform.asf.cache.IMemcachedCache;
import com.alisoft.xplatform.asf.cache.memcached.CacheUtil;
import com.alisoft.xplatform.asf.cache.memcached.MemcachedCacheManager;

/**
 * @author leeson 2014年12月3日 上午10:15:26 li_mr_ceo@163.com <br>
 * 
 */
public class MemCachedUtil {
	private static final Logger logger = Logger.getLogger(MemCachedUtil.class);
	private static final int REF_SECONDS = 180;
	private static ICacheManager<IMemcachedCache> manager;
	private static Map<String, IMemcachedCache> cacheArray = new HashMap<String, IMemcachedCache>();
	private static final String defalutCacheName = "mclient1";
	
	static {
		manager = CacheUtil.getCacheManager(IMemcachedCache.class, MemcachedCacheManager.class.getName());
		manager.setConfigFile("memcached.xml");
		manager.start();
		cacheArray.put(defalutCacheName, manager.getCache(defalutCacheName));
	}
	
	private static String getCacheName(String type, Object key) {
		StringBuffer cacheName = new StringBuffer(type);
		if (key != null)
			cacheName.append("_").append(key);

		return cacheName.toString();
	}

	public static void set(String type, Object key, Object value) {
		set(type, key, value, REF_SECONDS);
	}

	public static void putNoTimeInCache(String type, Object key, Object value) {
		if (value != null)
			set(type, key, value, -1);
	}

	public static void set(String type, Object key, Object value, int seconds) {
		if (value != null) {
			String cacheName = getCacheName(type, key);
			try {
				if (seconds < 1)
					((IMemcachedCache) cacheArray.get(defalutCacheName)).put(cacheName, value);
				else
					((IMemcachedCache) cacheArray.get(defalutCacheName)).put(cacheName, value, seconds);
			} catch (Exception e) {
				logger.log(Level.INFO, "cache mclient1 socket error。");
			}
		}
	}

	public static void delete(String type, Object key) {
		((IMemcachedCache) cacheArray.get(defalutCacheName)).remove(getCacheName(type, key));
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz, String type, Object key) {
		return (T)(((IMemcachedCache) cacheArray.get(defalutCacheName)).get(getCacheName(type, key)));
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(Class<T> clazz, String type, Object key) {
		return ((List<T>) ((IMemcachedCache) cacheArray.get(defalutCacheName)).get(getCacheName(type, key)));
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> clazz, String type, Object key, int localTTL) {
		try {
			return (T)((IMemcachedCache) cacheArray.get(defalutCacheName)).get(getCacheName(type, key), localTTL);
		} catch (Exception e) {
			
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getList(Class<T> clazz, String type, Object key, int localTTL) {
		try {
			return ((List<T>) ((IMemcachedCache) cacheArray.get(defalutCacheName)).get(getCacheName(type, key), localTTL));
		} catch (Exception e) {
			
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static <V> Map<String, V> getMap(Class<V> clazz, String type, Object key, int localTTL) {
		try {
			return ((Map<String, V>) ((IMemcachedCache) cacheArray.get(defalutCacheName)).get(getCacheName(type, key), localTTL));
		} catch (Exception e) {
			
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <V> Map<String, V> getMap(Class<V> clazz, String type, Object key) {
		try {
			return ((Map<String, V>) ((IMemcachedCache) cacheArray.get(defalutCacheName)).get(getCacheName(type, key)));
		} catch (Exception e) {
			
		}
		return null;
	}

	public static Set<String> getKeyList() {
		return ((IMemcachedCache) cacheArray.get(defalutCacheName)).keySet();
	}

	public static void clear() {
		((IMemcachedCache) cacheArray.get(defalutCacheName)).clear();
	}

	public static void close() {
		manager.stop();
	}
}