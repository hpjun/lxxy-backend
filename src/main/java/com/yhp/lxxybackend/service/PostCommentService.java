package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.PostComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhp.lxxybackend.model.vo.CommentVO;

import java.util.List;

/**
* @author Admin
* @description 针对表【post_comment(帖子评论)】的数据库操作Service
* @createDate 2024-03-27 17:22:54
*/
public interface PostCommentService extends IService<PostComment> {

    /**
     * 分页获得评论信息
     * @param postId
     * @param minTime
     * @param offset
     * @return
     */
    Result<List<CommentVO>> getPostComment(Integer postId, String minTime, Integer offset);
}
