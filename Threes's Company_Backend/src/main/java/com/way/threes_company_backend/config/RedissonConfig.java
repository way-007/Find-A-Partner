package com.way.threes_company_backend.config;


import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    // 01. 获取到对应的yml配置文件中的配置信息
    private String port;
    private String host;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 02. 配置连接redis
        String address = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(address).setDatabase(3);
        // 03. 创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
