package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.model.vo.PostVO;

import java.util.List;

/**
* @author Admin
* @description 针对表【post(帖子)】的数据库操作Service
* @createDate 2024-03-27 17:22:54
*/
public interface PostService extends IService<Post> {

    /**
     * 根据条件查询所有帖子
     * @return
     */
    Result<List<PostCardVO>> listPost(Integer pageNum, String sc, String postType);

    /**
     * 批量删除帖子
     * @param ids
     * @return
     */
    Result delete(List<Integer> ids);

    /**
     * 置顶/取消置顶
     * @param postId
     * @return
     */
    Result changeTop(Integer postId);

    /**
     * 发布帖子
     * @return
     */
    Result publish(PostDTO postDTO,String ip);


    /**
     * 用户端分类查询帖子
     * @param postType
     * @param minTime
     * @param offset
     * @return
     */
    Result<List<PostCardVO>> getPostByType(String postType, String minTime, Integer offset);

    /**
     * 获取帖子详情
     * @param postId
     * @return
     */
    Result<PostVO> postDetail(Integer postId);

    /**
     * 收藏帖子
     * @param postId
     * @return
     */
    Result favorite(Integer postId);

    /**
     * 取消收藏
     * @param postId
     * @return
     */
    Result unFavorite(Integer postId);
}
