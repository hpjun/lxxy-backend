package com.yhp.lxxybackend.model.vo;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/3/27 22:08
 */

@Data
public class UserCardVO {
    // admin
    private Integer id;				//用户id
    private String avatar;			//用户头像地址
    private String username;		//用户名
    private String phone;			//用户手机号
    private Boolean ban;				//账户状态
    private String createTime;	//账号创建时间

    // user
    private String profile;			//个人简介
    private Boolean isFollowFan;		//有没有关注该粉丝，粉丝列表的东西
    private Boolean isFollowTab;		//是不是关注列表，若为true，上面字段就无关紧要了
}
