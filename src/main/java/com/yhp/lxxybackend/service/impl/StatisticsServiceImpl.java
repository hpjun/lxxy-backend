package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
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
import com.yhp.lxxybackend.model.vo.HomeVO;
import com.yhp.lxxybackend.model.vo.PVUVData;
import com.yhp.lxxybackend.service.StatisticsService;
import com.yhp.lxxybackend.utils.BusinessUtils;
import io.swagger.models.auth.In;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author yhp
 * @date 2024/3/30 19:11
 */

@Service
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

        List<String> past7Days = BusinessUtils.getPast7Days();

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
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        for (PostType postType : postTypes) {
            postQueryWrapper.gt("create_time", startOfDay)
                    .eq("post_type_id", postType.getId());
            Long count = postMapper.selectCount(postQueryWrapper);
            newPostCountMap.put(postType.getTypeName(), count.intValue());
        }
        String newPostCount = new Gson().toJson(newPostCountMap);
        // 今日不同难度下的活动数
        QueryWrapper<Activity> activityQueryWrapper = new QueryWrapper<>();
        HashMap<String, Integer> newActivityCountMap = new HashMap<>();
        for (int i = 0; i < 3; i++) {
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
            List<String> past7Days = BusinessUtils.getPast7Days();
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
                pvuvData.setDate(split[1]+"-"+split[2]);
                pvuvDataList.add(pvuvData);
            }
            return Result.ok(pvuvDataList);
        } else if ("30day".equals(timeSpan)) {
            List<String> past30Days = BusinessUtils.getPast30Days();
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
                pvuvData.setDate(split[1]+"-"+split[2]);
                pvuvDataList.add(pvuvData);
            }
            return Result.ok(pvuvDataList);
        } else {
            return Result.fail("服务器错误");
        }
    }
}
