package com.way.threes_company_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 自定义redis的序列化方式
 */
@Configuration
public class RedisSerilizerConfig {
    @Bean
    public RedisTemplate<String, Object>  redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // 创建一个连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        // 设置序列化方式  默认是java自带的jdk序列化方式
        redisTemplate.setKeySerializer(RedisSerializer.string());
        return redisTemplate;
    }
}
