package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.PostComment;
import com.yhp.lxxybackend.model.vo.CommentVO;
import com.yhp.lxxybackend.service.PostCommentService;
import com.yhp.lxxybackend.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author Admin
* @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{

    @Resource
    PostCommentMapper postCommentMapper;

    @Override
    public Result<List<CommentVO>> getPostComment(
            Integer postId, String minTime, Integer offset) {

        QueryWrapper<PostComment> postCommentQueryWrapper = new QueryWrapper<>();
        // 将minTime转为Date类型方便查数据库
        long time = Long.parseLong(minTime);
        Date date = new Date(time);

        postCommentQueryWrapper
                .eq("post_id",postId)
                .lt("create_time", date)
                .orderByDesc("create_time");

        // 封装分页对象
        Page<PostComment> page = new Page<>(offset, MessageConstant.ADMIN_PAGE_SIZE);
        Page<PostComment> postCommentPage = postCommentMapper.selectPage(page, postCommentQueryWrapper);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<CommentVO> commentVOList = new ArrayList<>();
        List<PostComment> postComments = postCommentPage.getRecords();
        for (PostComment postComment : postComments) {
            CommentVO commentVO = BeanUtil.copyProperties(postComment, CommentVO.class);
            commentVO.setCreateTime(dateFormat.format(postComment.getCreateTime()));
            commentVO.setAvatar(postComment.getUserAvatar());
            commentVOList.add(commentVO);
        }
        return Result.ok(commentVOList);
    }
}




