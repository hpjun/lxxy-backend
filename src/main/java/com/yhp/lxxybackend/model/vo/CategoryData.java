package com.yhp.lxxybackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yhp
 * @date 2024/3/27 22:02
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryData {
    private String name;			//分类名称
    private Integer value;		//各分类值
}
