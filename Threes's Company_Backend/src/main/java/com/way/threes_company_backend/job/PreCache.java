package com.way.threes_company_backend.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.way.threes_company_backend.model.domain.User;
import com.way.threes_company_backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component   // 自动加入到spring的bean中
@Slf4j
public class PreCache {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    // 01. 不需要预热所有的用户  80万条数据  只需要预热主要的用户信息即可
    private List<Long> mainUser = Arrays.asList(8L);

    @Scheduled(cron = "0 16 22 * * *")
    public void doCacheRecommendUser() {
        // 获取到分布式锁的操作对象
        RLock lock = redissonClient.getLock("way:user:prechache:lock");
        try {
            if(lock.tryLock(0, 30000L, TimeUnit.MILLISECONDS)){
                System.out.println("get lock" + Thread.currentThread().getId());
                // 02. 遍历的方式获取重点用户中的userId
                for (Long userId : mainUser) {
                    // 03. 获取到主要用户的id
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    // 04. 查询数据库  分页查询
                    Page<User> userPage = userService.page(new Page<User>(1, 20), queryWrapper);
                    // 05. 设置key  redis中存储
                    String keyStrRedis = String.format("way:user:recommend:%s", userId);
                    // 06. 创建一个操作对象  redis
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(keyStrRedis, userPage, 40000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("valueOperations set is fail");
                    }
                }
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
