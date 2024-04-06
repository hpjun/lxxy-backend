package com.yhp.lxxybackend.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.mapper.ActivityMapper;
import com.yhp.lxxybackend.mapper.ActivityMemberMapper;
import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.model.entity.Activity;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.model.vo.CategoryData;
import com.yhp.lxxybackend.model.vo.UserRegionData;
import com.yhp.lxxybackend.utils.BusinessUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    @Resource
    ActivityMemberMapper activityMemberMapper;



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
        Long userCount = userMapper.selectCount(null);
        Long postCount = postMapper.selectCount(null);
        Long activityCount = activityMapper.selectCount(null);


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

        Long userCount = userMapper.selectCount(null);
        Long postCount = postMapper.selectCount(null);
        Long activityCount = activityMapper.selectCount(null);

        stringRedisTemplate.opsForValue().set(RedisConstants.DAY_USER_KEY+today,String.valueOf(userCount),RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(RedisConstants.DAY_ACTIVITY_KEY+today,String.valueOf(activityCount),RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(RedisConstants.DAY_POST_KEY+today,String.valueOf(postCount),RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .select("address","count(id) as value").
                groupBy("address")
                .orderByDesc("value");
        ArrayList<UserRegionData> userRegionDataList = new ArrayList<>();
        // 统计用户地域分布
        List<Map<String, Object>> maps = userMapper.selectMaps(userQueryWrapper);
        List<Map<String, Object>> maps1 = maps.subList(0, 10);
        for (Map<String, Object> map : maps1) {
            String address = (String) map.get("address");
            Long value = (Long) map.get("value");
            UserRegionData userRegionData = new UserRegionData();
            userRegionData.setRegion(address);
            userRegionData.setValue(Math.toIntExact(value));
            userRegionDataList.add(userRegionData);
        }
        String userRegion = new Gson().toJson(userRegionDataList);
        stringRedisTemplate.opsForValue().set(RedisConstants.USER_REGION_KEY,userRegion);

        // 统计活动参加率
        ArrayList<CategoryData> categoryDataList = new ArrayList<>();
        Integer totalCount = activityMapper.selectTotalCount();
        Long joinCount = activityMemberMapper.selectCount(null);
        CategoryData joinData = new CategoryData();
        CategoryData nullData = new CategoryData();
        joinData.setName("参加人数");
        joinData.setValue(Math.toIntExact(joinCount));
        nullData.setName("空缺人数");
        nullData.setValue((int) (totalCount-joinCount));
        categoryDataList.add(joinData);
        categoryDataList.add(nullData);
        String activityJoinRate = new Gson().toJson(categoryDataList);
        stringRedisTemplate.opsForValue().set(RedisConstants.ACTIVITY_JOIN_RATE,activityJoinRate);

        // 每天凌晨检查"hot:post"的长度，如果长度大于1000，则截断之后分数不高的数据
        // 获取当前热度表的长度
        long size = stringRedisTemplate.opsForZSet().size(RedisConstants.HOT_POST_KEY);
        // 如果长度超过500，则删除分数较低的内容
        if (size > 1000) {
            stringRedisTemplate.opsForZSet()
                    .removeRange(RedisConstants.HOT_POST_KEY, 0, size - 1001);
        }
    }

    /**
     * 每5分钟进行持久化
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void statisticsTask(){
        // 对帖子浏览量进行持久化
        Set<String> postKeys = stringRedisTemplate.keys(RedisConstants.POST_VIEW_COUNT+"*");
        if(postKeys != null){
            for (String postKey : postKeys) {
                String viewCount = stringRedisTemplate.opsForValue().get(postKey);
                String[] split = postKey.split(":");
                String postId = split[split.length - 1];
                Post post = postMapper.selectById(postId);
                if(post != null){
                    post.setViewCount(post.getViewCount()+Long.parseLong(viewCount));
                    postMapper.updateById(post);
                }
                stringRedisTemplate.delete(postKey);
            }
        }
    }
}
