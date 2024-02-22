package com.way.threes_company_backend.service;
import java.util.Date;

import com.way.threes_company_backend.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test() {
//        System.out.println("开启测试");
        // 01. 使用redisTemplate调用对应的方法去操作redis
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 02. 增
        valueOperations.set("wayString", "01");
        valueOperations.set("wayBoolean", true);
        valueOperations.set("wayFloat", 0.001);
        User user = new User();
        user.setUsername("你好");
        user.setUserAccount("wayyyds666");
        valueOperations.set("wayUser", user);

        // 03. 查
        Object result = valueOperations.get("wayString");
        Assertions.assertTrue("01".equals(result));

        result = valueOperations.get("wayBoolean");
        Assertions.assertTrue((Boolean)result);

        result = valueOperations.get("wayFloat");
        Assertions.assertTrue(0.001 == (Double)result);

    }
}
