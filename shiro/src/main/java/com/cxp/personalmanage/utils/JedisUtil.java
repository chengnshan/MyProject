package com.cxp.personalmanage.utils;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JedisUtil {

	private static JedisPool jedisPool;

	@Autowired
	public void setJedisPool(JedisPool jedisPool){
		this.jedisPool = jedisPool;
	}

	private static  Jedis getResource() {
		return jedisPool.getResource();
	}

	public static  byte[] set(byte[] key, byte[] value) {
		Jedis jedis = getResource();
		try {
			jedis.set(key, value);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return value;
	}

	public static  void expire(byte[] key, int expireTime) {
		Jedis jedis = getResource();
		try {
			jedis.expire(key, expireTime);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
	}

	public static  byte[] get(byte[] key) {
		Jedis jedis = getResource();
		try {
			return jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return null;
	}

	public static  void del(byte[] key) {
		Jedis jedis = getResource();
		try {
			jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
	}

	public static  Set<byte[]> keys(String key) {
		Jedis jedis = getResource();
		try {
			Set<byte[]> keys = jedis.keys((key + "#").getBytes());
			return keys;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		return null;
	}
}
