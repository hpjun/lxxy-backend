package com.yhp.lxxybackend.controller.user;

import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.model.dto.ActivityDTO;
import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author yhp
 * @date 2024/3/28 14:18
 */

@RestController("userActivityController")
@RequestMapping("/user/activity")
@Api(tags = "C端-活动管理接口")
@Slf4j
public class ActivityController {


    @GetMapping("/all")
    @ApiOperation("分页获取所有活动")
    public Result getAll(@RequestParam String minTime,
                         @RequestParam(required = false, defaultValue = "0") Integer offset) {
        // TODO 感觉可以调用相同的Service，因为权限校验已经由拦截器做完了
        return Result.ok("分页获取所有活动" + minTime + offset);
    }

    @GetMapping("/mine")
    @ApiOperation("分页获取我的活动")
    public Result getMine(@RequestParam String minTime,
                          @RequestParam(required = false, defaultValue = "0") Integer offset) {
        // TODO 感觉可以调用相同的Service，因为权限校验已经由拦截器做完了
        return Result.ok("分页获取我的活动" + minTime + offset);
    }

    @GetMapping("/joined")
    @ApiOperation("分页获取我参加的活动")
    public Result getJoined(@RequestParam String minTime,
                            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        // TODO 感觉可以调用相同的Service，因为权限校验已经由拦截器做完了
        return Result.ok("分页获取我参加的活动" + minTime + offset);
    }

    @PostMapping("/join/{activityId}")
    @ApiOperation("加入活动")
    public Result join(@PathVariable("activityId") Integer activityId){
        // TODO 加入活动
        return Result.ok("加入活动"+activityId);
    }

    @GetMapping("/{activityId}")
    @ApiOperation("获取活动详情")
    public Result activityDetail(@PathVariable("activityId") Integer activityId){
        // TODO 获取活动详情
        return Result.ok("获取活动详情"+activityId);
    }

    @PostMapping("/un-join/{activityId}")
    @ApiOperation("取消加入活动")
    public Result unJoin(@PathVariable("activityId") Integer activityId){
        // TODO 取消加入活动
        return Result.ok("取消加入活动"+activityId);
    }

    @PostMapping()
    @ApiOperation("创建活动")
    public Result createActivity(@RequestBody ActivityDTO activityDTO){
        // TODO 创建活动
        return Result.ok("创建活动"+activityDTO);
    }

    @GetMapping("/member/{activityId}")
    @ApiOperation("查看活动详细人数-前端未完成")
    public Result activityMember(@PathVariable("activityId") Integer activityId,
                                 @RequestParam Integer pageNum){
        // TODO 查看当前互动详细人数
        return Result.ok("查看当前互动详细人数"+activityId+pageNum+MessageConstant.USER_PAGE_SIZE+5);
    }

    @PutMapping("/{activityId}")
    @ApiOperation("我的活动编辑-前端未完成")
    public Result edit(@PathVariable("activityId") Integer activityId,
                       @RequestBody ActivityDTO activityDTO){
        // TODO 我的活动编辑
        return Result.ok("我的活动编辑"+activityId+activityDTO);
    }

    @DeleteMapping("/{activityId}")
    @ApiOperation("删除我的活动-前端未完成")
    public Result delete(@PathVariable("activityId") Integer activityId){
        // TODO 删除我的活动
        return Result.ok("删除我的活动"+activityId);
    }

}
