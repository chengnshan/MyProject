package com.cxp.personalmanage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {
	
	@Autowired
	private RedisProperties redisProperties;

	@Bean
	public JedisConnectionFactory factory() {
		JedisConnectionFactory factory = new JedisConnectionFactory();
		factory.setDatabase(0);
		factory.setPort(redisProperties.getPort());
		factory.setHostName(redisProperties.getHost());
		factory.setPassword(redisProperties.getPassword());
		factory.setDatabase(redisProperties.getDatabase());
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
		poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
		poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
		poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
		factory.setPoolConfig(poolConfig);
		return factory;
	}

	@Bean
	public JedisPool JedisPool() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
		poolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
		poolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
		poolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
		JedisPool jedisPool = new JedisPool(poolConfig, redisProperties.getHost(), redisProperties.getPort(), 60000,
				redisProperties.getPassword(), 0);
		return jedisPool;
	}
	
	@Bean(value = "stringRedisTemplate")
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

	@Bean(value = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
		// 创建一个模板类
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		// 将刚才的redis连接工厂设置到模板类中
		template.setConnectionFactory(factory);
		// 设置key的序列化器
		template.setKeySerializer(new StringRedisSerializer());
		// 设置value的序列化器
		// 使用Jackson 2，将对象序列化为JSON
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		// json转对象类，不设置默认的会将json转成hashmap
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);

		return template;
	}
}
