package com.yhp.lxxybackend.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yhp
 * @date 2024/3/27 22:47
 */

@Data
public class DynamicResult {
    private List<?> list;        //动态数据列表
    private Long minTime;        //最小时间戳，指定查询范围，固定范围后就可以固定下标了，第一次由前端查询当前时间
    private Integer offset;        //偏移量，给前端下次带上，便于跳过多少个元素，第一次spring指定0
}
