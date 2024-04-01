package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.*;

import java.util.List;

public interface StatisticsService {

    /**
     * 获取主页信息
     * @return
     */
    Result<HomeVO> getHomeData();

    /**
     * 获取pv、uv数据
     * @param timeSpan
     * @return
     */
    Result<List<PVUVData>> pvuv(String timeSpan);

    /**
     * 获取用户数量
     * @param timeSpan
     * @return
     */
    Result<List<UserData>> getUserCount(String timeSpan);

    /**
     * 获取活动数量
     * @param timeSpan
     * @return
     */
    Result<List<ActivityData>> getActivityCount(String timeSpan);

    /**
     * 获取帖子数量
     * @param timeSpan
     * @return
     */
    Result<List<PostData>> getPostCount(String timeSpan);

    /**
     * 获取不同板块下的帖子数
     * @return
     */
    Result<List<CategoryData>> postTypeCount();

    /**
     * 获取近7日活跃用户比例
     * @return
     */
    Result<List<CategoryData>> activeUserRate();

    /**
     * 获取不同难度下的活动数
     * @return
     */
    Result<List<CategoryData>> activityLevelRate();

    /**
     * 获取用户地域分布
     * @return
     */
    Result<List<UserRegionData>> userRegion();

    /**
     * 活动参加率
     * @return
     */
    Result<List<CategoryData>> activityJoinRate();
}
