package com.yhp.lxxybackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 活动
 * @TableName activity
 */
@TableName(value ="activity")
@Data
public class Activity implements Serializable {
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
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 活动简介
     */
    @TableField(value = "profile")
    private String profile;

    /**
     * 活动难度：0简单1休闲2困难
     */
    @TableField(value = "level")
    private Integer level;

    /**
     * 活动封面(json 数组)
     */
    @TableField(value = "pic_url")
    private String picUrl;

    /**
     * 集合点
     */
    @TableField(value = "venue")
    private String venue;

    /**
     * 活动路程KM
     */
    @TableField(value = "distance")
    private Integer distance;

    /**
     * 活动总人数
     */
    @TableField(value = "total_count")
    private Integer totalCount;

    /**
     * 创建用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    /**
     * 活动发起人手机号
     */
    @TableField(value = "contact")
    private String contact;

    /**
     * 活动状态：未开始、进行中、结束
     */
    @TableField(value = "status")
    private String status;

    /**
     * 是否置顶
     */
    @TableField(value = "is_top")
    private Integer isTop;

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