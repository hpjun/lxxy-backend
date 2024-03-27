package com.yhp.lxxybackend.model.vo;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/3/27 22:01
 */
@Data
public class ActivityData {
    String date;		//2024-3-25 19:57:00这种格式，传到前端，它自己会格式化
    Integer totalCount;	//活动总数
    Integer newCount;		//新增活动数
}
