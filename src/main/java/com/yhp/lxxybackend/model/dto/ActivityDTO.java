package com.yhp.lxxybackend.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:12
 */

@Data
public class ActivityDTO {
    private String title;                //活动名称
    private String profile;            //活动简介
    private String venue;                //集合地
    private Integer distance;        //里程
    private String level;                //难度，简单 休闲 困难
    private String startTime;            //开始时间 2024-04-02 17:41:00
    private String endTime;                //结束时间
    private Integer totalCount;    //总人数
    private String contact;            //联系方式
    private String picUrl;                //活动封面
}
