package com.yhp.lxxybackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 活动成员
 * @TableName activity_member
 */
@TableName(value ="activity_member")
@Data
public class ActivityMember implements Serializable {
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
     * 活动id
     */
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 参与用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 参与用户名称
     */
    @TableField(value = "username")
    private String username;

    /**
     * 参与用户头像
     */
    @TableField(value = "user_avatar")
    private String userAvatar;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}