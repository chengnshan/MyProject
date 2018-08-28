package com.cxp.personalmanage.shiro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import com.cxp.personalmanage.utils.JedisUtil;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

	private Logger logger = LoggerFactory.getLogger(RedisCache.class);

	private final String CACHE_SHIRO = "shiro_cache_";

	@Autowired
	private JedisUtil jedisUtil;

	private byte[] getKey(K k) {
		if (k instanceof String) {
			return (CACHE_SHIRO + k).getBytes();
		}
		return SerializationUtils.serialize(k);
	}

	@Override
	public V get(K key) throws CacheException {
		logger.info("get--从redis中获取:{}", key);
		byte[] value = jedisUtil.get(getKey(key));
		if (value != null) {
			return (V) SerializationUtils.deserialize(value);
		}
		return null;
	}

	@Override
	public V put(K k, V v) throws CacheException {
		logger.info("put--保存到redis，key:{},value:{}", k, v);
		byte[] key = getKey(k);
		byte[] value = SerializationUtils.serialize(v);
		jedisUtil.set(key, value);
		jedisUtil.expire(key, 600);
		return v;
	}

	@Override
	public V remove(K k) throws CacheException {
		logger.info("remove--删除key:{}", k);
		byte[] key = getKey(k);
		byte[] value = jedisUtil.get(key);
		jedisUtil.del(key);
		if (value != null) {
			return (V) SerializationUtils.deserialize(value);
		}
		return null;
	}

	@Override
	public void clear() throws CacheException {
		logger.info("clear--清空缓存");
		jedisUtil.del((CACHE_SHIRO + "*").getBytes());
	}

	@Override
	public int size() {
		logger.info("size--获取缓存大小");
		return keys().size();
	}

	@Override
	public Set<K> keys() {
		logger.info("keys--获取缓存大小keys");
		return (Set<K>) jedisUtil.keys(CACHE_SHIRO + "*");
	}

	@Override
	public Collection<V> values() {
		logger.info("values--获取缓存值values");
		Set<K> keys = keys();
		if (!CollectionUtils.isEmpty(keys)) {
			List<V> values = new ArrayList<V>(keys.size());
			for (K key : keys) {
				@SuppressWarnings("unchecked")
				V value = get(key);
				if (value != null) {
					values.add(value);
				}
			}
			return Collections.unmodifiableList(values);
		} else {
			return Collections.emptyList();
		}
	}
}
