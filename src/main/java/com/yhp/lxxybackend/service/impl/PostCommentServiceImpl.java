package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.exception.BusinessException;
import com.yhp.lxxybackend.mapper.FavoritesMapper;
import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.model.dto.CommentDTO;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.model.entity.PostComment;
import com.yhp.lxxybackend.model.vo.CommentVO;
import com.yhp.lxxybackend.service.PostCommentService;
import com.yhp.lxxybackend.mapper.PostCommentMapper;
import com.yhp.lxxybackend.utils.HotUtils;
import com.yhp.lxxybackend.utils.Ip2RegionUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Admin
 * @description 针对表【post_comment(帖子评论)】的数据库操作Service实现
 * @createDate 2024-03-27 17:22:54
 */
@Service
public class PostCommentServiceImpl extends ServiceImpl<PostCommentMapper, PostComment>
        implements PostCommentService {

    @Resource
    PostCommentMapper postCommentMapper;
    @Resource
    Ip2RegionUtils ip2RegionUtils;
    @Resource
    PostMapper postMapper;
    @Resource
    FavoritesMapper favoritesMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<List<CommentVO>> getPostComment(
            Integer postId, String minTime, Integer offset) {

        QueryWrapper<PostComment> postCommentQueryWrapper = new QueryWrapper<>();
        // 将minTime转为Date类型方便查数据库
        long time = Long.parseLong(minTime);
        Date date = new Date(time);

        postCommentQueryWrapper
                .eq("post_id", postId)
                .lt("create_time", date)
                .orderByDesc("create_time");

        // 封装分页对象
        Page<PostComment> page = new Page<>(offset, MessageConstant.ADMIN_PAGE_SIZE);
        Page<PostComment> postCommentPage = postCommentMapper.selectPage(page, postCommentQueryWrapper);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ArrayList<CommentVO> commentVOList = new ArrayList<>();
        List<PostComment> postComments = postCommentPage.getRecords();
        for (PostComment postComment : postComments) {
            CommentVO commentVO = BeanUtil.copyProperties(postComment, CommentVO.class);
            commentVO.setCreateTime(dateFormat.format(postComment.getCreateTime()));
            commentVO.setAvatar(postComment.getUserAvatar());
            commentVOList.add(commentVO);
        }
        return Result.ok(commentVOList);
    }

    @Override
    public Result writeComment(Integer postId, String commentStr, String ip) {
        // 校验评论字数(不超过200字)
        if (!(commentStr.length() > 0 && commentStr.length() < 200)) {
            return Result.fail(MessageConstant.CONTENT_TOO_LONG);
        }
        LoginUserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail(MessageConstant.POST_NOT_EXIST);
        }

        PostComment postComment = new PostComment();
        postComment.setPostId(Long.valueOf(postId));
        postComment.setComment(commentStr);
        postComment.setUserAvatar(user.getAvatar());
        postComment.setUsername(user.getUsername());
        postComment.setUserId(user.getId());

        // 获取该用户的ip属地
        try {
            Searcher searcher = ip2RegionUtils.getSearcher();
            String ipAddress = searcher.search(ip);    //中国|0|江苏省|泰州市|移动
            String[] split = ipAddress.split("\\|");
            postComment.setIpRegion(split[2]);
        } catch (Exception e) {
            postComment.setIpRegion("未知");
        }

        postCommentMapper.insert(postComment);
        // 帖子评论数+1和更新lc时间
        post.setCommentCount(post.getCommentCount() + 1);
        post.setLcTime(new Date());
        postMapper.updateById(post);

        // 评论完成之后就要更新分数
        HotUtils.addPostHot(postId, RedisConstants.COMMENT_SCORE, favoritesMapper, postMapper, stringRedisTemplate);
        return Result.ok("发布成功");
    }
}




