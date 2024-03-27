package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.service.PostService;
import com.yhp.lxxybackend.mapper.PostMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【post(帖子)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

}




