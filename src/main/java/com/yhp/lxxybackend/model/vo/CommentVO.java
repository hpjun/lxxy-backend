package com.yhp.lxxybackend.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:43
 */

@Data
public class CommentVO {
    // user
    private Integer id;            //评论id
    private Integer userId;        //用户id
    private String username;        //用户名
    private String avatar;        //用户头像
    private String ipRegion;        //IP属地
    private String comment;        //评论内容
    private Date createTime;        //发布时间
}
