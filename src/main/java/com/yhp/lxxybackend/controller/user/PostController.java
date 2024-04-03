package com.yhp.lxxybackend.controller.user;

import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.model.vo.PostVO;
import com.yhp.lxxybackend.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 13:50
 */

@RestController("userPostController")
@RequestMapping("/user/post")
@Api(tags = "C端-帖子管理接口")
@Slf4j
public class PostController {

    @Resource
    PostService postService;

    @GetMapping("/random/next")
    @ApiOperation("主页获得随机帖子")
    public Result getPostCard(){
        // TODO 随机推荐算法
        return Result.ok("主页获得随机帖子");
    }

    @GetMapping("/latest")
    @ApiOperation("分类获取帖子")
    public Result<List<PostCardVO>> getPostByType(@RequestParam String postType,
                          @RequestParam String minTime,
                          @RequestParam Integer offset){
        return postService.getPostByType(postType,minTime,offset);
    }

    @GetMapping("/{postId}")
    @ApiOperation("获取帖子详情")
    public Result<PostVO> postDetail(@PathVariable("postId") Integer postId){
        // TODO 获取帖子详情
        return postService.postDetail(postId);
    }

    @PostMapping("/favorite/{postId}")
    @ApiOperation("收藏帖子")
    public Result favorite(@PathVariable("postId") Integer postId){
        // TODO 收藏帖子
        return Result.ok("收藏帖子"+postId);
    }

    @PostMapping("/un-favorite/{postId}")
    @ApiOperation("取消收藏帖子")
    public Result unFavorite(@PathVariable("postId") Integer postId){
        // TODO 取消收藏帖子
        return Result.ok("取消收藏帖子"+postId);
    }

    @PostMapping()
    @ApiOperation("发布帖子")
    public Result publishPost(@RequestBody PostDTO postDTO){
        // TODO 发布帖子
        return Result.ok("发布帖子"+postDTO);
    }

    @PutMapping("/{postId}")
    @ApiOperation("我的帖子编辑-前端未完成")
    public Result edit(@PathVariable("postId") Integer postId,
                       @RequestBody PostDTO postDTO){
        // TODO 我的帖子编辑
        return Result.ok("我的帖子编辑"+postId+postDTO);
    }

    @DeleteMapping("/{postId}")
    @ApiOperation("删除我的帖子-前端未完成")
    public Result delete(@PathVariable("postId") Integer postId){
        // TODO 删除我的帖子
        return Result.ok("删除我的帖子"+postId);
    }
}
