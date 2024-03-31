package com.yhp.lxxybackend.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.mapper.ActivityMapper;
import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.model.entity.Activity;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.utils.BusinessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author yhp
 * @date 2024/3/31 14:42
 */

@Component
@Slf4j
public class StatisticsTask {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserMapper userMapper;
    @Resource
    PostMapper postMapper;
    @Resource
    ActivityMapper activityMapper;

    QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>().eq("is_delete",0);
    QueryWrapper<Post> postQueryWrapper = new QueryWrapper<Post>().eq("is_delete",0);
    QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<Activity>().eq("is_delete",0);


    /**
     * 每小时进行数据统计
     * 包括：PV、UV、用户数量、活动数量、帖子数量
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourTask(){
        // 当前的小时数
        String hour = BusinessUtils.getHour();
        // UV看来只能进行覆盖操作，即先删除之前的数据然后在创建一个数据（这里执行删除操作，拦截器里执行创建操作）
        stringRedisTemplate.expire(RedisConstants.HOUR_UV_KEY+hour,0, TimeUnit.SECONDS);
        // PV、用户数量、活动数量、帖子数量可以往list里塞
        // 将总记录数塞入当前list中，同时删除右边数据

        String pv = stringRedisTemplate.opsForValue().get(RedisConstants.TOTAL_PV_KEY);
        Long userCount = userMapper.selectCount(userQueryWrapper);
        Long postCount = postMapper.selectCount(postQueryWrapper);
        Long activityCount = activityMapper.selectCount(activityQueryWrapper);


        stringRedisTemplate.opsForList().leftPush(RedisConstants.HOUR_PV_KEY,pv);
        if(stringRedisTemplate.opsForList().size(RedisConstants.HOUR_PV_KEY)>25){
            stringRedisTemplate.opsForList().rightPop(RedisConstants.HOUR_PV_KEY);
        }
        stringRedisTemplate.opsForList().leftPush(RedisConstants.HOUR_POST_KEY,String.valueOf(postCount));
        if(stringRedisTemplate.opsForList().size(RedisConstants.HOUR_POST_KEY)>25){
            stringRedisTemplate.opsForList().rightPop(RedisConstants.HOUR_POST_KEY);
        }
        stringRedisTemplate.opsForList().leftPush(RedisConstants.HOUR_USER_KEY,String.valueOf(userCount));
        if(stringRedisTemplate.opsForList().size(RedisConstants.HOUR_USER_KEY)>25){
            stringRedisTemplate.opsForList().rightPop(RedisConstants.HOUR_USER_KEY);
        }
        stringRedisTemplate.opsForList().leftPush(RedisConstants.HOUR_ACTIVITY_KEY,String.valueOf(activityCount));
        if(stringRedisTemplate.opsForList().size(RedisConstants.HOUR_ACTIVITY_KEY)>25){
            stringRedisTemplate.opsForList().rightPop(RedisConstants.HOUR_ACTIVITY_KEY);
        }

    }

    /**
     * 每天凌晨开始进行数据统计
     * 包括：PV、用户数量、活动数量、帖子数量
     */
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void dayTask(){
        // 每天凌晨将这些数据拿到，statistics:day:{pv/userCount/postCount/activityCount}:{2024:3:31}
        String today = BusinessUtils.getToday();

        Long userCount = userMapper.selectCount(userQueryWrapper);
        Long postCount = postMapper.selectCount(postQueryWrapper);
        Long activityCount = activityMapper.selectCount(activityQueryWrapper);

        stringRedisTemplate.opsForValue().set(RedisConstants.DAY_USER_KEY+today,String.valueOf(userCount),RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(RedisConstants.DAY_ACTIVITY_KEY+today,String.valueOf(activityCount),RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(RedisConstants.DAY_POST_KEY+today,String.valueOf(postCount),RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);

    }
}
