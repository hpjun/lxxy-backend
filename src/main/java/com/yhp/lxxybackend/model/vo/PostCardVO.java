package com.yhp.lxxybackend.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/27 22:03
 */

@Data
public class PostCardVO {

    // admin需要的字段
    private Integer id;                        //帖子id
    private String title;                    //帖子标题
    private String content;                //帖子内容
    private String username;                //发布者用户名
    private String postType;            //帖子板块
    private Integer viewCount;            //浏览量
    private Integer commentCount;    //评论数
    private String createTime;            //创建时间
    private Boolean isTop;                    //是否置顶



    // user需要的字段
    private String type;            // "post"/"activity"
    private Integer userId;			//创建的用户id
    private String userAvatar;		//用户头像
    private String lcTime;				//最后评论时间
    private List<String> picUrlList;	//帖子图片列表
}
