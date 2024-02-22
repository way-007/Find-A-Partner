package com.way.threes_company_backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.way.threes_company_backend.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 测试看门狗是否能够进行自动的续期操作  默认锁的时间是30s，每10s续期一次
 */
@SpringBootTest
@Slf4j
public class WatchDogTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void testDog() {
        // 获取到分布式锁的操作对象
        RLock lock = redissonClient.getLock("way:user:prechache:lock");
        try {
            if(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){
                Thread.sleep(3000000);
                System.out.println("get lock" + Thread.currentThread().getId());
                // 02. 遍历的方式获取重点用户中的userId
//                for (Long userId : mainUser) {
//                    // 03. 获取到主要用户的id
//                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//                    // 04. 查询数据库  分页查询
//                    Page<User> userPage = userService.page(new Page<User>(1, 20), queryWrapper);
//                    // 05. 设置key  redis中存储
//                    String keyStrRedis = String.format("way:user:recommend:%s", userId);
//                    // 06. 创建一个操作对象  redis
//                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//                    try {
//                        valueOperations.set(keyStrRedis, userPage, 40000, TimeUnit.MILLISECONDS);
//                    } catch (Exception e) {
//                        log.error("valueOperations set is fail");
//                    }
//                }
            }
        } catch (Exception e) {
            log.error("trylock is error", e);
        } finally {   // 释放锁必须写在finally里面  因为如果前面的代码报错了就不会执行释放锁   所以必须让锁过期
            if(lock.isHeldByCurrentThread()) {
                System.out.println("forgive lock" + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
