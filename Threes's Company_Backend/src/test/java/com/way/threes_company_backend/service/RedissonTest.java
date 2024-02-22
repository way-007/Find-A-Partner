package com.way.threes_company_backend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class RedissonTest {


    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {
        RList<String> rLlist = redissonClient.getList("test-list");   // 指定一个键
//        rLlist.add("xiaowu");
        System.out.println(rLlist.get(0));
        rLlist.remove(1);
    }
}
