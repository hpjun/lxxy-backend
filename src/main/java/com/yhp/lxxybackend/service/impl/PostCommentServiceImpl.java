package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.model.entity.PostComment;
import com.yhp.lxxybackend.service.PostCommentService;
import com.yhp.lxxybackend.mapper.PostCommentMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
    implements PostCommentService{

}




