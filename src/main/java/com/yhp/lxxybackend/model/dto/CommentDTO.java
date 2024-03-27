package com.yhp.lxxybackend.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author yhp
 * @date 2024/3/27 22:45
 */

@Data
public class CommentDTO {
    private Integer postId;		//帖子id
    private Integer userId;		//用户id
    private String username;		//用户名
    private String avatar;		//用户头像
    private String ipRegion;		//IP属地
    private String comment;		//评论内容
    private Date createTime;		//发布时间
}
