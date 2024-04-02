package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.PostType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhp.lxxybackend.model.vo.PostTypeVO;

import java.util.List;

/**
* @author Admin
* @description 针对表【post_type(帖子板块)】的数据库操作Service
* @createDate 2024-03-27 17:22:54
*/
public interface PostTypeService extends IService<PostType> {

    /**
     * 分页获取板块信息
     * @param pageNum
     * @return
     */
    Result<List<PostTypeVO>> listPostType(Integer pageNum);

    /**
     * 新增板块
     * @param typeName
     * @return
     */
    Result add(String typeName);

    /**
     * 编辑板块名称
     * @param postTypeId
     * @param typeName
     * @return
     */
    Result edit(Integer postTypeId, String typeName);

    /**
     * 禁用/启用板块
     * @param postTypeId
     * @return
     */
    Result changeStatus(Integer postTypeId);
}
