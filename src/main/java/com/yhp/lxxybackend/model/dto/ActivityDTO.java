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
    private Integer level;                //难度，0简单1休闲2困难
    private Date startTime;            //开始时间
    private Date endTime;                //结束时间
    private Integer totalCount;    //总人数
    private String contact;            //联系方式
    private String picUrl;                //活动封面
}
