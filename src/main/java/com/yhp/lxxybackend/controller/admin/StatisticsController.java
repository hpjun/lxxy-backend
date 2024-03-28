package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yhp
 * @date 2024/3/28 17:39
 */

@RestController("adminStatisticsController")
@RequestMapping("/admin/statistics")
@Api(tags = "统计接口")
@Slf4j
public class StatisticsController {

    @GetMapping("/pvuv/{timeSpan}")
    @ApiOperation("获取PV、UV数据")
    public Result pvuv(@PathVariable("timeSpan") String timeSpan){
        // TODO 获取PV、UV数据
        return Result.ok("获取PV、UV数据"+timeSpan);
    }

    @GetMapping("/user-count/{timeSpan}")
    @ApiOperation("获取用户数量")
    public Result userCount(@PathVariable("timeSpan") String timeSpan){
        // TODO 获取用户数量
        return Result.ok("获取用户数量"+timeSpan);
    }

    @GetMapping("/activity-count/{timeSpan}")
    @ApiOperation("获取活动数量")
    public Result activityCount(@PathVariable("timeSpan") String timeSpan){
        // TODO 获取活动数量
        return Result.ok("获取活动数量"+timeSpan);
    }

    @GetMapping("/post-count/{timeSpan}")
    @ApiOperation("获取帖子数量")
    public Result postCount(@PathVariable("timeSpan") String timeSpan){
        // TODO 获取帖子数量
        return Result.ok("获取帖子数量"+timeSpan);
    }

    @GetMapping("/post-type-count")
    @ApiOperation("获取不同板块下的帖子数量")
    public Result postTypeCount(){
        // TODO 获取不同板块下的帖子数量,指所有板块
        return Result.ok("获取不同板块下的帖子数量");
    }

    @GetMapping("/active-user-rate")
    @ApiOperation("获取近7日活跃用户比例")
    public Result activeUserRate(){
        // TODO 获取近7日活跃用户比例
        return Result.ok("获取近7日活跃用户比例");
    }

    @GetMapping("/activity-level-rate")
    @ApiOperation("获取不同难度下的活动数")
    public Result activityLevelRate(){
        // TODO 获取不同难度下的活动数
        return Result.ok("获取不同难度下的活动数");
    }

    @GetMapping("/activity-join-rate")
    @ApiOperation("获取截至昨天活动参加率")
    public Result activityJoinRate(){
        // TODO 获取截至昨天活动参加率
        return Result.ok("获取截至昨天活动参加率");
    }

    @GetMapping("/user-region")
    @ApiOperation("获取用户地域分布")
    public Result userRegion(){
        // TODO 获取用户地域分布
        return Result.ok("获取用户地域分布");
    }

}
