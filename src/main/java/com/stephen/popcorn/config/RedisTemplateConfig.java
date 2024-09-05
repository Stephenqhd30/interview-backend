package com.stephen.popcorn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis的配置类
 * 用于配置 RedisTemplate，以便在 Spring 应用中使用 Redis
 *
 * @author: stephen qiu
 **/
// @Configuration
public class RedisTemplateConfig {
	
	// @Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
	/**
	 * 配置 RedisTemplate 实例
	 *
	 * @return 配置好的 RedisTemplate 实例
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		// 设置 Redis 连接工厂
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		// 使用Jackson2JsonRedisSerializer作为值的序列化器
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		redisTemplate.setHashKeySerializer(new StringRedisSerializer());
		redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
		redisTemplate.afterPropertiesSet();
		
		return redisTemplate;
	}
	
}
