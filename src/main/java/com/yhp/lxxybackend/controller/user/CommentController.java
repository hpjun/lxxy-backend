package com.yhp.lxxybackend.controller.user;

import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.model.dto.CommentDTO;
import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.ResolutionSyntax;

/**
 * @author yhp
 * @date 2024/3/28 15:36
 */

@RestController("userCommentController")
@RequestMapping("/user/comment")
@Api(tags = "C端-评论接口")
@Slf4j
public class CommentController {


    @GetMapping("/{postId}")
    @ApiOperation("获取帖子的评论")
    public Result getPostComment(@PathVariable("postId") Integer postId,
                                 @RequestParam String minTime,
                                 @RequestParam(required = false, defaultValue = "0") Integer offset) {
        // TODO 获取帖子的评论
        return  Result.ok("获取主页关注列表"+postId+minTime+offset+ MessageConstant.USER_PAGE_SIZE+5);
    }

    @PostMapping("/{postId}")
    @ApiOperation("发布帖子评论")
    public Result writeComment(@PathVariable("postId") Integer postId,
                               @RequestBody CommentDTO commentDTO){
        // TODO 发布帖子评论，需要将该帖子的lc_time更新一下
        return Result.ok("发布帖子评论"+postId+commentDTO);
    }

}
