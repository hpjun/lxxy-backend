package com.yhp.lxxybackend.model.vo;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/3/27 22:07
 */

@Data
public class PostTypeVO {
    private Integer id;					//板块id
    private String typeName;			//板块名称
    private Boolean status;			//板块状态
    private String updateTime;		//板块更新时间
}
