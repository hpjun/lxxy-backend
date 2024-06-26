package com.yhp.lxxybackend.mapper;

import com.yhp.lxxybackend.model.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yhp.lxxybackend.model.vo.CategoryData;

import java.util.List;

/**
* @author Admin
* @description 针对表【post(帖子)】的数据库操作Mapper
* @createDate 2024-03-27 17:22:54
* @Entity com.yhp.lxxybackend.entity.Post
*/
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 根据板块查询帖子数量
     * @return
     */
    List<CategoryData> selectCountByType();
}




