package com.yhp.lxxybackend.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author yhp
 * @date 2024/3/28 16:39
 */

@Component
public class SystemListener implements ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    Long startTime;
    Long endTime;
    String redisKey = "statistics:system-run-time";


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 获取开始时间戳
        startTime = System.currentTimeMillis();
        // score是开始时间，member是结束时间（一开始就先置为开始时间）
        stringRedisTemplate.opsForZSet().add(redisKey, startTime.toString(), startTime);
    }

    @Override
    public void onApplicationEvent(@NotNull ContextClosedEvent event) {
        // 获取结束时间戳
        endTime = System.currentTimeMillis();

        // 先删除本次开始的member，因为开始时间和结束时间都相同
        stringRedisTemplate.opsForZSet().remove(redisKey,startTime.toString());
        // score是开始时间，member是结束时间
        stringRedisTemplate.opsForZSet().add(redisKey, endTime.toString(), startTime);
    }
}
