package com.yhp.lxxybackend.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:05
 */

@Data
public class ActivityCardVO {

    // admin
    private Integer id;				//活动id
    private String title;			//活动标题
    private String profile;		//活动简介
    private String venue;			//集合点
    private String level;			//活动难度
    private Integer distance;	//距离
    private String startTime;	//开始时间
    private String endTime;		//结束时间

    // user
    private String picUrl;		//活动封面
    private Integer totalCount;	//最大人数
    private Date createTime;		//创建时间，用来解决动态数据查询问题
    private Integer memberCount;	//成员数
    private String status;		//活动状态：x个月后、x天后、即将开始（小于3天）、进行中、结束
}
