package com.cxp.personalmanage.shiro;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisCacheManager implements CacheManager {

	private final ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();

	@Autowired
	private RedisCache redisCache;

	@Override
	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
		Cache cache = caches.get(name);
		if (cache == null) {
			cache = new RedisCache<K, V>();
			caches.put(name, cache);
		}
		return cache;
	}

}
