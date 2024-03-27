package com.yhp.lxxybackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子评论
 * @TableName post_comment
 */
@TableName(value ="post_comment")
@Data
public class PostComment implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 评论用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 被评论帖子id
     */
    @TableField(value = "post_id")
    private Long postId;

    /**
     * ip属地
     */
    @TableField(value = "ip_region")
    private String ipRegion;

    /**
     * 创建用户名称
     */
    @TableField(value = "username")
    private String username;

    /**
     * 创建用户头像
     */
    @TableField(value = "user_avatar")
    private String userAvatar;

    /**
     * 评论内容最大字数1000
     */
    @TableField(value = "comment")
    private String comment;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}