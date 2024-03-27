package com.yhp.lxxybackend.model.vo;


import lombok.Data;

@Data
public class HomeVO {
    private String systemStartTime;          //系统开始运行的时间戳，13位，非10位
    private Integer PV;                      //今日网页浏览量
    private Integer UV;                      //今日UV
    private Integer activeUser;             //今日活跃用户数
    private Integer activeUser7Day;         //近7日活跃用户数
    private Integer newUser;                 //今日新增用户
    private Integer trail;                  //今日骑行足迹板块新增数
    private Integer equipment;              //今日装备讨论新增数
    private Integer exchange;               //今日二手交易新增数
    private Integer qa;                      //今日有问必答新增数
    private Integer easy;                    //今日简单活动新增数
    private Integer relax;                   //今日休闲活动新增数
    private Integer difficulty;              //今日困难活动新增数
}
