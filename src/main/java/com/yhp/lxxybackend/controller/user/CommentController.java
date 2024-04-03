package com.yhp.lxxybackend.controller.user;

import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.model.dto.CommentDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.CommentVO;
import com.yhp.lxxybackend.service.PostCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.print.attribute.ResolutionSyntax;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 15:36
 */

@RestController("userCommentController")
@RequestMapping("/user/comment")
@Api(tags = "C端-评论接口")
@Slf4j
public class CommentController {

    @Resource
    PostCommentService postCommentService;

    @GetMapping("/{postId}")
    @ApiOperation("获取帖子的评论")
    public Result<List<CommentVO>> getPostComment(@PathVariable("postId") Integer postId,
                                                  @RequestParam String minTime,
                                                  @RequestParam Integer offset) {
        // TODO 获取帖子的评论
        return  postCommentService.getPostComment(postId,minTime,offset);
    }

    @PostMapping("/{postId}")
    @ApiOperation("发布帖子评论")
    public Result writeComment(@PathVariable("postId") Integer postId,
                               @RequestBody CommentDTO commentDTO){
        // TODO 发布帖子评论，需要将该帖子的lc_time更新一下
        return Result.ok("发布帖子评论"+postId+commentDTO);
    }

}
