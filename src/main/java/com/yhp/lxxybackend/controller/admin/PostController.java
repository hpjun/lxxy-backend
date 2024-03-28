package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 17:53
 */

@RestController("adminPostController")
@RequestMapping("/admin/post")
@Api(tags = "帖子接口")
@Slf4j
public class PostController {

    @GetMapping("/list")
    @ApiOperation("分页获取帖子信息")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam String sc,
                       @RequestParam String postType){
        // TODO 分页获取帖子信息，兼查询
        return Result.ok("分页获取帖子信息，兼查询"+pageNum+sc+postType);
    }


    @DeleteMapping("/delete")
    @ApiOperation("批量删除帖子")
    public Result delete(@RequestParam List<Integer> ids){
        // TODO 批量删除帖子
        return Result.ok("批量删除帖子"+ids);
    }

    @PostMapping()
    @ApiOperation("发布帖子")
    public Result publish(@RequestBody PostDTO postDTO){
        // TODO 发布帖子
        return Result.ok("发布帖子"+postDTO);
    }

    @PutMapping("/{postId}")
    @ApiOperation("修改帖子-前端未完成")
    public Result edit(@PathVariable("postId") Integer postId,
                       @RequestBody PostDTO postDTO){
        // TODO 修改帖子
        return Result.ok("修改帖子"+postId+postDTO);
    }

    @GetMapping("/{postId}")
    @ApiOperation("获取帖子详情-前端未完成")
    public Result postDetail(@PathVariable("postId") Integer postId){
        // TODO 获取帖子详情
        return Result.ok("获取帖子详情"+postId);
    }

}
