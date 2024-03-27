package com.yhp.lxxybackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
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
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    private String birthday;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 用户头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 用户简介
     */
    @TableField(value = "profile")
    private String profile;

    /**
     * 用户角色：user/admin
     */
    @TableField(value = "role")
    private String role;

    /**
     * 性别：男、女、保密
     */
    @TableField(value = "sex")
    private String sex;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 学校
     */
    @TableField(value = "school")
    private String school;

    /**
     * 班级
     */
    @TableField(value = "u_class")
    private String uClass;

    /**
     * 是否封禁
     */
    @TableField(value = "ban")
    private Integer ban;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}