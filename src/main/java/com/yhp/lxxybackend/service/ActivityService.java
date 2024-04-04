package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.ActivityDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Activity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhp.lxxybackend.model.vo.ActivityCardVO;
import com.yhp.lxxybackend.model.vo.ActivityVO;

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

    /**
     * 分页获取所有活动
     * @param minTime
     * @param offset
     * @return
     */
    Result<List<ActivityCardVO>> getAll(String minTime, Integer offset);

    /**
     * 获取活动详情
     * @param activityId
     * @return
     */
    Result<ActivityVO> activityDetail(Integer activityId);

    /**
     * 参加活动
     * @param activityId
     * @return
     */
    Result join(Integer activityId);

    /**
     * 取消参加活动
     * @param activityId
     * @return
     */
    Result unJoin(Integer activityId);

    /**
     * 分页获取我创建的活动
     * @param offset
     * @return
     */
    Result<List<ActivityCardVO>> getMine(Integer offset);

    /**
     * 分页获取我参加的活动
     * @param offset
     * @return
     */
    Result<List<ActivityCardVO>> getJoined(Integer offset);
}
