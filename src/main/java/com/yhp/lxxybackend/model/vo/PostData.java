package com.yhp.lxxybackend.model.vo;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/3/27 22:02
 */

@Data
public class PostData {
    private String date;					//2024-3-25 19:57:00这种格式，传到前端，它自己会格式化
    private Integer totalCount;	//帖子总数
    private Integer newCount;		//新增帖子数
}
