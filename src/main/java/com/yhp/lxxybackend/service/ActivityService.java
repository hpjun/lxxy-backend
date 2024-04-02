package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.ActivityDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhp.lxxybackend.model.vo.ActivityCardVO;

import java.util.List;

/**
* @author Admin
* @description 针对表【activity(活动)】的数据库操作Service
* @createDate 2024-03-27 17:22:54
*/
public interface ActivityService extends IService<Activity> {

    /**
     * 分页获取活动信息
     * @param pageNum
     * @param sc
     * @param level
     * @return
     */
    Result<List<ActivityCardVO>> listActivity(Integer pageNum, String sc, String level);

    /**
     * 批量删除活动
     * @param ids
     * @return
     */
    Result delete(List<Integer> ids);

    /**
     * 创建活动信息
     * @param activityDTO
     * @return
     */
    Result publish(ActivityDTO activityDTO);
}
