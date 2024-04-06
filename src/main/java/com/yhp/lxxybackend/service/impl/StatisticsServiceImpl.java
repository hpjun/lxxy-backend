package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.mapper.ActivityMapper;
import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.mapper.PostTypeMapper;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Activity;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.model.entity.PostType;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.model.vo.*;
import com.yhp.lxxybackend.service.StatisticsService;
import com.yhp.lxxybackend.utils.BusinessUtils;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author yhp
 * @date 2024/3/30 19:11
 */

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserMapper userMapper;
    @Resource
    PostMapper postMapper;
    @Resource
    ActivityMapper activityMapper;
    @Resource
    PostTypeMapper postTypeMapper;

    @Override
    public Result<HomeVO> getHomeData() {
//        查询Redis，获取当前系统开始运行的时间、当日PV、UV，当日活跃用户数、近7日活跃用户数（HyperLoglog）
//        查询数据库，将日期定到今日凌晨到现在，查询新增用户数、每个板块的新增帖子数、每个难度活动的新增活动数

        List<String> past7Days = BusinessUtils.getPastNDays(7);

        // Redis
        // 系统运行时间
        Set<String> strings = stringRedisTemplate.opsForZSet().reverseRangeByScore("statistics:system-run-time", 0, new Date().getTime());
        String lastTime = strings.stream().iterator().next();
        // 当日PV(statistics:pv:{2024:3:30},string)
        String pv = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_PV_KEY + past7Days.get(0));
        // 当日UV(statistics:pv:{2024:3:30},hyperloglog)
        Long uv = stringRedisTemplate.opsForHyperLogLog().size(RedisConstants.DAY_UV_KEY + past7Days.get(0));
        // 当前活跃人数(statistics:activeUser:{2024:3:30},hyperloglog)
        Long activeUser = uv;
        // 近7日活跃人数(statistics:activeUser:{2024:3:30},hyperloglog)直接合并方便统计
        Long activeUser7Day = stringRedisTemplate.opsForHyperLogLog().size(
                RedisConstants.DAY_UV_KEY + past7Days.get(0),
                RedisConstants.DAY_UV_KEY + past7Days.get(1),
                RedisConstants.DAY_UV_KEY + past7Days.get(2),
                RedisConstants.DAY_UV_KEY + past7Days.get(3),
                RedisConstants.DAY_UV_KEY + past7Days.get(4),
                RedisConstants.DAY_UV_KEY + past7Days.get(5),
                RedisConstants.DAY_UV_KEY + past7Days.get(6)
        );
        // Mysql
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        // 今日新增用户数
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<User>();
        userQueryWrapper.gt("create_time", startOfDay);
        Long newUser = userMapper.selectCount(userQueryWrapper);
        // 今日每个板块新增帖子数
        Map<String, Integer> newPostCountMap = new HashMap<>();
        QueryWrapper<PostType> postTypeQueryWrapper = new QueryWrapper<>();
        postTypeQueryWrapper.eq("is_delete", 0)
                .eq("status", 1);
        List<PostType> postTypes = postTypeMapper.selectList(postTypeQueryWrapper);
        for (PostType postType : postTypes) {
            QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
            postQueryWrapper.gt("create_time", startOfDay)
                    .eq("post_type_id", postType.getId());
            Long count = postMapper.selectCount(postQueryWrapper);
            newPostCountMap.put(postType.getTypeName(), count.intValue());
        }
        String newPostCount = new Gson().toJson(newPostCountMap);
        // 今日不同难度下的活动数
        HashMap<String, Integer> newActivityCountMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<>();
            activityQueryWrapper
                    .gt("create_time", startOfDay)
                    .eq("level", i);
            Long count = activityMapper.selectCount(activityQueryWrapper);
            newActivityCountMap.put(String.valueOf(i), count.intValue());
        }
        String newActivityCount = new Gson().toJson(newActivityCountMap);


        HomeVO homeVO = new HomeVO();
        homeVO.setSystemStartTime(lastTime);
        homeVO.setPV(Long.parseLong(pv));
        homeVO.setUV(uv);
        homeVO.setActiveUser(activeUser);
        homeVO.setActiveUser7Day(activeUser7Day);
        homeVO.setNewUser(newUser.intValue());
        homeVO.setNewPostCount(newPostCount);
        homeVO.setNewActivityCount(newActivityCount);
        return Result.ok(homeVO);
    }

    @Override
    public Result<List<PVUVData>> pvuv(String timeSpan) {

        ArrayList<Integer> uvCount = new ArrayList<>();
        ArrayList<Integer> pvCount = new ArrayList<>();
        ArrayList<PVUVData> pvuvDataList = new ArrayList<>();

        String format = "%02d"; // 格式化为2位数字，不足2位补零
        // 判断timeSpan的类型在做查询
        if ("24h".equals(timeSpan)) {
            // 查询UV
            List<String> past24Hour = BusinessUtils.getPast24Hour();
            for (String time : past24Hour) {
                Long size = stringRedisTemplate.opsForHyperLogLog()
                        .size(RedisConstants.HOUR_UV_KEY + time);
                uvCount.add(size.intValue());
            }
            // 查询PV
            List<String> range = stringRedisTemplate.opsForList().range(RedisConstants.HOUR_PV_KEY, 0, -1);
            // 去除最后一个元素，然后翻转
            range.remove(range.size() - 1);
            Collections.reverse(range);

            int i = 0;
            for (String time : past24Hour) {
                PVUVData pvuvData = new PVUVData();
                pvuvData.setDate(time + ":00");
                pvuvData.setPV(Integer.valueOf(range.get(i)));
                pvuvData.setUV(uvCount.get(i++));
                pvuvDataList.add(pvuvData);
            }
            return Result.ok(pvuvDataList);
        } else if ("7day".equals(timeSpan)) {
            List<String> past7Days = BusinessUtils.getPastNDays(7);
            Collections.reverse(past7Days);
            // 查询UV和PV
            for (String time : past7Days) {
                PVUVData pvuvData = new PVUVData();
                Long uv = stringRedisTemplate.opsForHyperLogLog().size(RedisConstants.DAY_UV_KEY + time);
                String pv = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_PV_KEY + time);
                pv = (pv == null) ? "0" : pv;
                pvuvData.setUV(Math.toIntExact(uv));
                pvuvData.setPV(Integer.valueOf(pv));
                String[] split = time.split(":");
                pvuvData.setDate(split[1] + "-" + split[2]);
                pvuvDataList.add(pvuvData);
            }
            return Result.ok(pvuvDataList);
        } else if ("30day".equals(timeSpan)) {
            List<String> past30Days = BusinessUtils.getPastNDays(30);
            Collections.reverse(past30Days);
            // 查询UV和PV
            for (String time : past30Days) {
                PVUVData pvuvData = new PVUVData();
                Long uv = stringRedisTemplate.opsForHyperLogLog().size(RedisConstants.DAY_UV_KEY + time);
                String pv = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_PV_KEY + time);
                pv = (pv == null) ? "0" : pv;
                pvuvData.setUV(Math.toIntExact(uv));
                pvuvData.setPV(Integer.valueOf(pv));
                String[] split = time.split(":");
                pvuvData.setDate(split[1] + "-" + split[2]);
                pvuvDataList.add(pvuvData);
            }
            return Result.ok(pvuvDataList);
        } else {
            return Result.fail("服务器错误");
        }
    }

    @Override
    public Result<List<UserData>> getUserCount(String timeSpan) {

        ArrayList<UserData> userDataList = new ArrayList<>();

        if ("24h".equals(timeSpan)) {
            List<String> past24Hour = BusinessUtils.getPast24Hour();
            List<String> totalUserCount = stringRedisTemplate.opsForList().range(RedisConstants.HOUR_USER_KEY, 0, -1);
            ArrayList<Integer> newUserCount = new ArrayList<>();

            // 获取新增用户数
            for (int i = 0; i < totalUserCount.size() - 1; i++) {
                Integer todayCount = Integer.valueOf(totalUserCount.get(i));
                Integer yesterDayCount = Integer.valueOf(totalUserCount.get(i + 1));
                newUserCount.add(todayCount - yesterDayCount);
            }
            Collections.reverse(newUserCount);

            // 去除最后一个元素，然后翻转，获取总用户数
            totalUserCount.remove(totalUserCount.size() - 1);
            Collections.reverse(totalUserCount);

            // 封装UserData
            int i = 0;
            for (String time : past24Hour) {
                UserData userData = new UserData();
                userData.setDate(time + ":00");
                userData.setNewCount(newUserCount.get(i));
                userData.setTotalCount(Integer.valueOf(totalUserCount.get(i++)));
                userDataList.add(userData);
            }
            return Result.ok(userDataList);
        } else if ("7day".equals(timeSpan)) {
            List<String> past8Days = BusinessUtils.getPastNDays(8);
            ArrayList<Integer> totalUserCount = new ArrayList<>();
            ArrayList<Integer> newUserCount = new ArrayList<>();

            for (String time : past8Days) {
                String userCount = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_USER_KEY + time);
                userCount = userCount == null ? "0" : userCount;
                totalUserCount.add(Integer.valueOf(userCount));
            }

            for (int i = 0; i < totalUserCount.size() - 1; i++) {
                Integer todayCount = totalUserCount.get(i);
                Integer yesterDay = totalUserCount.get(i + 1);
                newUserCount.add(todayCount - yesterDay);
            }
            Collections.reverse(newUserCount);

            totalUserCount.remove(totalUserCount.size() - 1);
            Collections.reverse(totalUserCount);
            Collections.reverse(past8Days);

            for (int i = 0; i < 7; i++) {
                UserData userData = new UserData();
                String[] split = past8Days.get(i).split(":");
                userData.setDate(split[1] + "-" + split[2]);
                userData.setNewCount(newUserCount.get(i));
                userData.setTotalCount(totalUserCount.get(i));
                userDataList.add(userData);
            }
            return Result.ok(userDataList);
        } else if ("30day".equals(timeSpan)) {
            List<String> past31Days = BusinessUtils.getPastNDays(31);
            ArrayList<Integer> totalUserCount = new ArrayList<>();
            ArrayList<Integer> newUserCount = new ArrayList<>();

            for (String time : past31Days) {
                String userCount = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_USER_KEY + time);
                userCount = userCount == null ? "0" : userCount;
                totalUserCount.add(Integer.valueOf(userCount));
            }

            for (int i = 0; i < totalUserCount.size() - 1; i++) {
                Integer todayCount = totalUserCount.get(i);
                Integer yesterDay = totalUserCount.get(i + 1);
                newUserCount.add(todayCount - yesterDay);
            }
            Collections.reverse(newUserCount);

            totalUserCount.remove(totalUserCount.size() - 1);
            Collections.reverse(totalUserCount);
            Collections.reverse(past31Days);

            for (int i = 0; i < 30; i++) {
                UserData userData = new UserData();
                String[] split = past31Days.get(i).split(":");
                userData.setDate(split[1] + "-" + split[2]);
                userData.setNewCount(newUserCount.get(i));
                userData.setTotalCount(totalUserCount.get(i));
                userDataList.add(userData);
            }
            return Result.ok(userDataList);
        } else {
            return Result.fail("服务器错误");
        }
    }

    @Override
    public Result<List<ActivityData>> getActivityCount(String timeSpan) {

        ArrayList<ActivityData> activityDataList = new ArrayList<>();

        if ("24h".equals(timeSpan)) {
            List<String> past24Hour = BusinessUtils.getPast24Hour();
            List<String> totalActivityCount = stringRedisTemplate.opsForList().range(RedisConstants.HOUR_ACTIVITY_KEY, 0, -1);
            ArrayList<Integer> newActivityCount = new ArrayList<>();

            // 获取新增用户数
            for (int i = 0; i < totalActivityCount.size() - 1; i++) {
                Integer todayCount = Integer.valueOf(totalActivityCount.get(i));
                Integer yesterDayCount = Integer.valueOf(totalActivityCount.get(i + 1));
                newActivityCount.add(todayCount - yesterDayCount);
            }
            Collections.reverse(newActivityCount);

            // 去除最后一个元素，然后翻转，获取总用户数
            totalActivityCount.remove(totalActivityCount.size() - 1);
            Collections.reverse(totalActivityCount);

            // 封装ActivityData
            int i = 0;
            for (String time : past24Hour) {
                ActivityData activityData = new ActivityData();
                activityData.setDate(time + ":00");
                activityData.setNewCount(newActivityCount.get(i));
                activityData.setTotalCount(Integer.valueOf(totalActivityCount.get(i++)));
                activityDataList.add(activityData);
            }
            return Result.ok(activityDataList);
        } else if ("7day".equals(timeSpan)) {
            List<String> past8Days = BusinessUtils.getPastNDays(8);
            ArrayList<Integer> totalActivityCount = new ArrayList<>();
            ArrayList<Integer> newActivityCount = new ArrayList<>();

            for (String time : past8Days) {
                String activityCount = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_ACTIVITY_KEY + time);
                activityCount = activityCount == null ? "0" : activityCount;
                totalActivityCount.add(Integer.valueOf(activityCount));
            }

            for (int i = 0; i < totalActivityCount.size() - 1; i++) {
                Integer todayCount = totalActivityCount.get(i);
                Integer yesterDay = totalActivityCount.get(i + 1);
                newActivityCount.add(todayCount - yesterDay);
            }
            Collections.reverse(newActivityCount);

            totalActivityCount.remove(totalActivityCount.size() - 1);
            Collections.reverse(totalActivityCount);
            Collections.reverse(past8Days);

            for (int i = 0; i < 7; i++) {
                ActivityData activityData = new ActivityData();
                String[] split = past8Days.get(i).split(":");
                activityData.setDate(split[1] + "-" + split[2]);
                activityData.setNewCount(newActivityCount.get(i));
                activityData.setTotalCount(totalActivityCount.get(i));
                activityDataList.add(activityData);
            }
            return Result.ok(activityDataList);
        } else if ("30day".equals(timeSpan)) {
            List<String> past31Days = BusinessUtils.getPastNDays(31);
            ArrayList<Integer> totalActivityCount = new ArrayList<>();
            ArrayList<Integer> newActivityCount = new ArrayList<>();

            for (String time : past31Days) {
                String activityCount = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_ACTIVITY_KEY + time);
                activityCount = activityCount == null ? "0" : activityCount;
                totalActivityCount.add(Integer.valueOf(activityCount));
            }

            for (int i = 0; i < totalActivityCount.size() - 1; i++) {
                Integer todayCount = totalActivityCount.get(i);
                Integer yesterDay = totalActivityCount.get(i + 1);
                newActivityCount.add(todayCount - yesterDay);
            }
            Collections.reverse(newActivityCount);

            totalActivityCount.remove(totalActivityCount.size() - 1);
            Collections.reverse(totalActivityCount);
            Collections.reverse(past31Days);

            for (int i = 0; i < 30; i++) {
                ActivityData activityData = new ActivityData();
                String[] split = past31Days.get(i).split(":");
                activityData.setDate(split[1] + "-" + split[2]);
                activityData.setNewCount(newActivityCount.get(i));
                activityData.setTotalCount(totalActivityCount.get(i));
                activityDataList.add(activityData);
            }
            return Result.ok(activityDataList);
        } else {
            return Result.fail("服务器错误");
        }
    }

    @Override
    public Result<List<PostData>> getPostCount(String timeSpan) {

        ArrayList<PostData> postDataList = new ArrayList<>();

        if ("24h".equals(timeSpan)) {
            List<String> past24Hour = BusinessUtils.getPast24Hour();
            List<String> totalPostCount = stringRedisTemplate.opsForList().range(RedisConstants.HOUR_POST_KEY, 0, -1);
            ArrayList<Integer> newPostCount = new ArrayList<>();

            // 获取新增用户数
            for (int i = 0; i < totalPostCount.size() - 1; i++) {
                Integer todayCount = Integer.valueOf(totalPostCount.get(i));
                Integer yesterDayCount = Integer.valueOf(totalPostCount.get(i + 1));
                newPostCount.add(todayCount - yesterDayCount);
            }
            Collections.reverse(newPostCount);

            // 去除最后一个元素，然后翻转，获取总用户数
            totalPostCount.remove(totalPostCount.size() - 1);
            Collections.reverse(totalPostCount);

            // 封装PostData
            int i = 0;
            for (String time : past24Hour) {
                PostData postData = new PostData();
                postData.setDate(time + ":00");
                postData.setNewCount(newPostCount.get(i));
                postData.setTotalCount(Integer.valueOf(totalPostCount.get(i++)));
                postDataList.add(postData);
            }
            return Result.ok(postDataList);
        } else if ("7day".equals(timeSpan)) {
            List<String> past8Days = BusinessUtils.getPastNDays(8);
            ArrayList<Integer> totalPostCount = new ArrayList<>();
            ArrayList<Integer> newPostCount = new ArrayList<>();

            for (String time : past8Days) {
                String postCount = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_POST_KEY + time);
                postCount = postCount == null ? "0" : postCount;
                totalPostCount.add(Integer.valueOf(postCount));
            }

            for (int i = 0; i < totalPostCount.size() - 1; i++) {
                Integer todayCount = totalPostCount.get(i);
                Integer yesterDay = totalPostCount.get(i + 1);
                newPostCount.add(todayCount - yesterDay);
            }
            Collections.reverse(newPostCount);

            totalPostCount.remove(totalPostCount.size() - 1);
            Collections.reverse(totalPostCount);
            Collections.reverse(past8Days);

            for (int i = 0; i < 7; i++) {
                PostData postData = new PostData();
                String[] split = past8Days.get(i).split(":");
                postData.setDate(split[1] + "-" + split[2]);
                postData.setNewCount(newPostCount.get(i));
                postData.setTotalCount(totalPostCount.get(i));
                postDataList.add(postData);
            }
            return Result.ok(postDataList);
        } else if ("30day".equals(timeSpan)) {
            List<String> past8Days = BusinessUtils.getPastNDays(31);
            ArrayList<Integer> totalPostCount = new ArrayList<>();
            ArrayList<Integer> newPostCount = new ArrayList<>();

            for (String time : past8Days) {
                String postCount = stringRedisTemplate.opsForValue().get(RedisConstants.DAY_POST_KEY + time);
                postCount = postCount == null ? "0" : postCount;
                totalPostCount.add(Integer.valueOf(postCount));
            }

            for (int i = 0; i < totalPostCount.size() - 1; i++) {
                Integer todayCount = totalPostCount.get(i);
                Integer yesterDay = totalPostCount.get(i + 1);
                newPostCount.add(todayCount - yesterDay);
            }
            Collections.reverse(newPostCount);

            totalPostCount.remove(totalPostCount.size() - 1);
            Collections.reverse(totalPostCount);
            Collections.reverse(past8Days);

            for (int i = 0; i < 30; i++) {
                PostData postData = new PostData();
                String[] split = past8Days.get(i).split(":");
                postData.setDate(split[1] + "-" + split[2]);
                postData.setNewCount(newPostCount.get(i));
                postData.setTotalCount(totalPostCount.get(i));
                postDataList.add(postData);
            }
            return Result.ok(postDataList);
        } else {
            return Result.fail("服务器错误");
        }
    }

    @Override
    public Result<List<CategoryData>> postTypeCount() {
        List<CategoryData> categoryDataList = postMapper.selectCountByType();
        return Result.ok(categoryDataList);
    }

    @Override
    public Result<List<CategoryData>> activeUserRate() {
        ArrayList<CategoryData> categoryDataList = new ArrayList<>();
        // 获取活跃用户数
        Set<String> keys = stringRedisTemplate.keys(RedisConstants.LOGIN_USER_KEY + "*");
        HashSet<String> activeCount = new HashSet<>();
        assert keys != null;
        keys.forEach(key -> {
            String userId = key.split(":")[2].substring(19);
            activeCount.add(userId);
        });
        // 获取总用户数
        Long totalCount = userMapper.selectCount(null);
        CategoryData activeUser = new CategoryData("活跃用户", activeCount.size());
        CategoryData un = new CategoryData("未活跃用户", (int) (totalCount - activeCount.size()));
        categoryDataList.add(activeUser);
        categoryDataList.add(un);
        return Result.ok(categoryDataList);
    }

    @Override
    public Result<List<CategoryData>> activityLevelRate() {
        ArrayList<CategoryData> categoryDataList = new ArrayList<>();
        QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<>();
        activityQueryWrapper.groupBy("level")
                .select("level", "count(*) as count");
        List<Map<String, Object>> activities = activityMapper.selectMaps(activityQueryWrapper);
        for (Map<String, Object> activity : activities) {
            Integer level = (Integer) activity.get("level");
            String levelName = null;
            Long count = (Long) activity.get("count");
            if (level == 0) {
                levelName = "简单";
            }
            if (level == 1) {
                levelName = "休闲";
            }
            if (level == 2) {
                levelName = "困难";
            }
            CategoryData categoryData = new CategoryData(levelName, Math.toIntExact(count));
            categoryDataList.add(categoryData);
        }
        return Result.ok(categoryDataList);
    }

    @Override
    public Result<List<UserRegionData>> userRegion() {
        // 从Redis中获取用户地域分布
        String userRegionStr = stringRedisTemplate.opsForValue().get(RedisConstants.USER_REGION_KEY);
        Type type = new TypeToken<List<UserRegionData>>() {
        }.getType();
        List<UserRegionData> userRegionData = new Gson().fromJson(userRegionStr, type);
        return Result.ok(userRegionData);
    }

    @Override
    public Result<List<CategoryData>> activityJoinRate() {
        // 从Redis获取活动参加率
        String activityJoinRateStr = stringRedisTemplate.opsForValue().get(RedisConstants.ACTIVITY_JOIN_RATE);
        Type type = new TypeToken<List<CategoryData>>() {
        }.getType();
        List<CategoryData> activityJoinRate = new Gson().fromJson(activityJoinRateStr, type);
        return Result.ok(activityJoinRate);
    }
}
