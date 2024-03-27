package com.yhp.lxxybackend.model.vo;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/3/27 21:57
 */
@Data
public class UserData {
    private String date;		//2024-3-25 19:57:00这种格式，传到前端，它自己会格式化
    private Integer totalCount;	//用户总数
    private Integer newCount;	//新增用户数
}
