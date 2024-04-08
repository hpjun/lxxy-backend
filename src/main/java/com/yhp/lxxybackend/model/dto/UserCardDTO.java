package com.yhp.lxxybackend.model.dto;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/4/4 20:13
 */

@Data
public class UserCardDTO {
    private Long userId;        //用户id
    private String username;		//用户名
    private String avatar;		//用户头像
    private String profile;		//简介
    private Boolean isFollow;		//是否关注粉丝，就是回关
    private Boolean isFollowTab;        //当前是否是关注接口
}
