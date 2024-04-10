package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @Resource
    PostService postService;

    @GetMapping("/list")
    @ApiOperation("分页获取帖子信息")
    public Result<List<PostCardVO>> list(@RequestParam Integer pageNum,
                                         @RequestParam String sc,
                                         @RequestParam String postType){
        return postService.listPost(pageNum,sc,postType);
    }


    @DeleteMapping("/delete")
    @ApiOperation("批量删除帖子")
    public Result delete(@RequestBody List<Integer> ids){
        return postService.delete(ids);
    }

    @PostMapping()
    @ApiOperation("发布帖子")
    public Result publish(@RequestBody PostDTO postDTO, HttpServletRequest request){
        String ip = request.getRemoteAddr();
        // TODO 上线取消，本地测试环境，ip先固定
        ip = "223.104.151.72";
        return postService.publish(postDTO,ip);
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

    @PutMapping("/top/{postId}")
    @ApiOperation("置顶/取消置顶")
    public Result changeTop(@PathVariable("postId") Integer postId){
        return postService.changeTop(postId);
    }

}
