package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.*;
import com.yhp.lxxybackend.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 17:39
 */

@RestController("adminStatisticsController")
@RequestMapping("/admin/statistics")
@Api(tags = "统计接口")
@Slf4j
public class StatisticsController {

    @Resource
    StatisticsService statisticsService;

    @GetMapping("/pvuv/{timeSpan}")
    @ApiOperation("获取PV、UV数据")
    public Result<List<PVUVData>> pvuv(@PathVariable("timeSpan") String timeSpan){
        return statisticsService.pvuv(timeSpan);
    }

    @GetMapping("/user-count/{timeSpan}")
    @ApiOperation("获取用户数量")
    public Result<List<UserData>> getUserCount(@PathVariable("timeSpan") String timeSpan){
        return statisticsService.getUserCount(timeSpan);
    }

    @GetMapping("/activity-count/{timeSpan}")
    @ApiOperation("获取活动数量")
    public Result<List<ActivityData>> getActivityCount(@PathVariable("timeSpan") String timeSpan){
        return statisticsService.getActivityCount(timeSpan);
    }

    @GetMapping("/post-count/{timeSpan}")
    @ApiOperation("获取帖子数量")
    public Result<List<PostData>> getPostCount(@PathVariable("timeSpan") String timeSpan){
        return statisticsService.getPostCount(timeSpan);
    }

    @GetMapping("/post-type-count")
    @ApiOperation("获取不同板块下的帖子数量")
    public Result<List<CategoryData>> postTypeCount(){
        return statisticsService.postTypeCount();
    }

    @GetMapping("/active-user-rate")
    @ApiOperation("获取近7日活跃用户比例")
    public Result<List<CategoryData>> activeUserRate(){
        return statisticsService.activeUserRate();
    }

    @GetMapping("/activity-level-rate")
    @ApiOperation("获取不同难度下的活动数")
    public Result<List<CategoryData>> activityLevelRate(){
        return statisticsService.activityLevelRate();
    }

    @GetMapping("/activity-join-rate")
    @ApiOperation("获取截至昨天活动参加率")
    public Result activityJoinRate(){
        // TODO 获取截至昨天活动参加率
        return Result.ok("获取截至昨天活动参加率");
    }

    @GetMapping("/user-region")
    @ApiOperation("获取用户地域分布")
    public Result<List<UserRegionData>> userRegion(){
        // TODO 获取用户地域分布
        return statisticsService.userRegion();
    }

}
