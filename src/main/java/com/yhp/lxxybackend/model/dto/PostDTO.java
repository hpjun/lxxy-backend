package com.yhp.lxxybackend.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author yhp
 * @date 2024/3/27 22:11
 */

@Data
public class PostDTO {
    private String postType;                //帖子板块
    private String title;                        //帖子标题
    private String content;                    //帖子内容
    private List<String> picUrlList;    //帖子图片地址列表
}
