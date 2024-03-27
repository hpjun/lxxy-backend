package com.yhp.lxxybackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 帖子
 * @TableName post
 */
@TableName(value ="post")
@Data
public class Post implements Serializable {
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
     * 是否删除
     */
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * ip属地
     */
    @TableField(value = "ip_region")
    private String ipRegion;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 帖子图片(json 数组)
     */
    @TableField(value = "pic_url_list")
    private String picUrlList;

    /**
     * 评论数
     */
    @TableField(value = "comment_count")
    private Long commentCount;

    /**
     * 浏览量
     */
    @TableField(value = "view_count")
    private Long viewCount;

    /**
     * 帖子板块id
     */
    @TableField(value = "post_type_id")
    private Integer postTypeId;

    /**
     * 创建用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 是否置顶
     */
    @TableField(value = "is_top")
    private Integer isTop;

    /**
     * 最近评论时间
     */
    @TableField(value = "lc_time")
    private Date lcTime;

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
     * 是否可见
     */
    @TableField(value = "is_show")
    private Integer isShow;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}