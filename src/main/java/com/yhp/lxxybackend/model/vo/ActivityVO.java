package com.yhp.lxxybackend.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:06
 */

@Data
public class ActivityVO {
    // admin
    private Integer id;                    //id
    private String picUrl;                //活动封面地址
    private String title;                //活动标题
    private String venue;                //集合点
    private String contact;            //电话
    private Integer level;                //难度 0简单1休闲2困难
    private String profile;        //简介
    private Integer distance;        //距离
    private Integer totalCount;    //总人数
    private Date startTime;            //开始时间
    private Date endTime;                //结束时间
    private Integer memberCount;//当前成员数，需要查表

    // user
    private Boolean isJoin;		//当前用户是否加入，需要查表
}
