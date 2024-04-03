package com.yhp.lxxybackend.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/27 22:04
 */

@Data
public class PostVO {
    // admin
    private Integer id;                            //帖子id
    private Integer userId;                    //用户id
    private String title;                        //帖子标题
    private String username;                    //用户名
    private String avatar;                        //用户头像地址
    private String createTime;                    //帖子创建时间
    private String ipRegion;                    //IP属地
    private String postType;                //帖子板块
    private String content;                    //帖子内容
    private List<String> picUrlList;    //帖子图片集合

    // user
    private Boolean isFavorite;		//该用户是否收藏帖子
}
