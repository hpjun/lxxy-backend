package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.HomeVO;
import com.yhp.lxxybackend.model.vo.PVUVData;

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
}
