package com.yhp.lxxybackend.mapper;

import com.yhp.lxxybackend.model.entity.Activity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Admin
* @description 针对表【activity(活动)】的数据库操作Mapper
* @createDate 2024-03-27 17:22:54
* @Entity com.yhp.lxxybackend.entity.Activity
*/
public interface ActivityMapper extends BaseMapper<Activity> {

    /**
     * 获取所有活动总人数
     * @return
     */
    Integer selectTotalCount();
}




