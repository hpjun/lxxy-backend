package com.yhp.lxxybackend.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.mapper.FavoritesMapper;
import com.yhp.lxxybackend.mapper.PostCommentMapper;
import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.model.entity.Favorites;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.model.entity.PostComment;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * @author yhp
 * @date 2024/4/6 16:43
 */


public class HotUtils {
    public static void addPostHot(Integer postId,Double hot,
                                  FavoritesMapper favoritesMapper,
                                  PostMapper postMapper,
                                  StringRedisTemplate stringRedisTemplate){
        // 每当有浏览+0.1、评论+20、收藏+30的动作就更新热度zset
        Double s = stringRedisTemplate.opsForZSet().score(RedisConstants.HOT_POST_KEY, String.valueOf(postId));
        // 判断该集合中是否有该帖子(hot:post)
        if(s == null){
            // 热度表没有，初次进行添加到热度表
            Post post = postMapper.selectById(postId);
            if(post == null){
                return;
            }
            Long favoritesCount = favoritesMapper.selectCount(new QueryWrapper<Favorites>().eq("post_id", postId));
            Double score = post.getViewCount()*0.1 + post.getCommentCount()*20 + favoritesCount *30;
            stringRedisTemplate.opsForZSet().add(RedisConstants.HOT_POST_KEY, String.valueOf(postId),score);
            return;
        }
        stringRedisTemplate.opsForZSet().incrementScore(RedisConstants.HOT_POST_KEY, String.valueOf(postId),hot);
    }
}
