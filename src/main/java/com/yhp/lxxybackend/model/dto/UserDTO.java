package com.yhp.lxxybackend.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:13
 */

@Data
public class UserDTO {
    // admin
    private String username;		//用户名
    private String avatar;		//头像
    private String profile;		//简介

    private Integer sex;			//性别
    private Date birthday;		//生日
    private String address;		//用户地址

    private String newPassword;	//新密码



    // user
    private String code;			//验证码
}
