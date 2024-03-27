package com.yhp.lxxybackend.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:08
 */

@Data
public class UserVO {
    // admin
    private Integer id;                        //用户id
    private String username;                //用户名
    private String avatar;                    //头像
    private String profile;            //简介

    private Integer fansCount;            //粉丝人数
    private Integer followsCount;    //关注人数

    private Integer sex;                    //性别
    private Date birthday;                    //生日
    private String address;                //用户地址



    // user
    private Boolean isFollow;		//是否关注
}
