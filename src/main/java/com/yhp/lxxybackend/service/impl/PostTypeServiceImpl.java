package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.model.entity.PostType;
import com.yhp.lxxybackend.service.PostTypeService;
import com.yhp.lxxybackend.mapper.PostTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【post_type(帖子板块)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class PostTypeServiceImpl extends ServiceImpl<PostTypeMapper, PostType>
    implements PostTypeService{

}




