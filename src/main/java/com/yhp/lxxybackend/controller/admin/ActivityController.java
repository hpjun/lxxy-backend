package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.ActivityDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.ActivityCardVO;
import com.yhp.lxxybackend.service.ActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 19:14
 */

@RestController("adminActivityController")
@RequestMapping("/admin/activity")
@Api(tags = "活动接口")
@Slf4j
public class ActivityController {

    @Resource
    ActivityService activityService;

    @GetMapping("/list")
    @ApiOperation("分页获取活动信息")
    public Result<List<ActivityCardVO>> list(@RequestParam Integer pageNum,
                                             @RequestParam String sc,
                                             @RequestParam String level){
        return activityService.listActivity(pageNum,sc,level);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除活动")
    public Result delete(@RequestBody List<Integer> ids){
        return activityService.delete(ids);
    }

    @PostMapping()
    @ApiOperation("创建活动")
    public Result publish(@RequestBody ActivityDTO activityDTO){
        // TODO 创建活动，创建的时候自己也要参加该活动
        return activityService.publish(activityDTO);
    }

    @PutMapping("/{activityId}")
    @ApiOperation("修改活动信息-前端未完成")
    public Result edit(@PathVariable("activityId") Integer activityId,
                       @RequestBody ActivityDTO activityDTO){
        // TODO 修改活动信息
        return Result.ok("修改活动信息"+activityId+activityDTO);
    }

    @GetMapping("/{activityId}")
    @ApiOperation("获取活动详细信息-前端未完成")
    public Result activityDetail(@PathVariable("activityId") Integer activityId){
        // TODO 获取活动详细信息
        return Result.ok("获取活动详细信息"+activityId);
    }


}
