package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.BusinessConstant;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.exception.BusinessException;
import com.yhp.lxxybackend.mapper.*;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.*;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.model.vo.PostVO;
import com.yhp.lxxybackend.service.PostCommentService;
import com.yhp.lxxybackend.service.PostService;
import com.yhp.lxxybackend.utils.HotUtils;
import com.yhp.lxxybackend.utils.Ip2RegionUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import javafx.geometry.Pos;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Admin
 * @description 针对表【post(帖子)】的数据库操作Service实现
 * @createDate 2024-03-27 17:22:54
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
        implements PostService {

    @Resource
    PostMapper postMapper;
    @Resource
    PostTypeMapper postTypeMapper;
    @Resource
    PostCommentMapper postCommentMapper;
    @Resource
    Ip2RegionUtils ip2RegionUtils;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FavoritesMapper favoritesMapper;
    @Resource
    FollowMapper followMapper;


    @Override
    public Result<List<PostCardVO>> listPost(Integer pageNum, String sc, String postType) {
        // 封装查询条件
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        QueryWrapper<PostType> postTypeQueryWrapper = new QueryWrapper<>();
        postTypeQueryWrapper.eq("type_name", postType);
        PostType postTypeName = postTypeMapper.selectOne(postTypeQueryWrapper);
        if (postTypeName != null) {
            // 有该板块，就加入条件，没有就不加等于全查
            Long postTypeId = postTypeName.getId();
            postQueryWrapper.eq("post_type_id", postTypeId);
        }
        if (!StrUtil.isBlank(sc)) {
            postQueryWrapper
                    .and(qw -> qw
                            .like("id", sc)
                            .or().like("content", sc)
                            .or().like("username", sc));
        }
        // 封装分页对象
        Page<Post> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        // 分页查询
        Page<Post> postPage = postMapper.selectPage(page, postQueryWrapper);
        List<Post> posts = postPage.getRecords();
        ArrayList<PostCardVO> postCardVOList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Post post : posts) {
            PostCardVO postCardVO = BeanUtil.copyProperties(post, PostCardVO.class);
            // 格式化时间
            postCardVO.setCreateTime(dateFormat.format(post.getCreateTime()));
            // 查询板块名称
            String typeName = postTypeMapper.selectById(post.getPostTypeId()).getTypeName();
            postCardVO.setPostType(typeName);
            postCardVOList.add(postCardVO);
        }

        return Result.ok(postCardVOList, page.getTotal());
    }

    @Override
    public Result delete(List<Integer> ids) {
        // 判断ids是否为空
        if (ids.size() == 0) {
            return Result.fail("帖子不存在，删除失败");
        }

        for (Integer id : ids) {
            // 删除帖子
            postMapper.deleteById(id);
            // 删除评论
            postCommentMapper.delete(new QueryWrapper<PostComment>()
                    .eq("post_id", id));
        }
        return Result.ok("删除成功");
    }

    @Override
    public Result changeTop(Integer postId) {
        // 查询该帖子信息
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail("该帖子不存在");
        }
        Integer isTop = post.getIsTop();
        if (isTop == 1) {
            // 取消置顶
            post.setIsTop(0);
            postMapper.updateById(post);
            return Result.ok(false);
        } else {
            // 置顶
            post.setIsTop(1);
            postMapper.updateById(post);
            return Result.ok(true);
        }
    }

    @Override
    public Result publish(PostDTO postDTO, String ip) {

        // 获取当前登录用户信息，将信息封装到Post中
        LoginUserDTO user = UserHolder.getUser();

        if(user == null){
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        String postTypeName = postDTO.getPostType();
        String title = postDTO.getTitle();
        String content = postDTO.getContent();
        List<String> picUrlList = postDTO.getPicUrlList();
        // 校验信息 帖子板块(是否存在，是否被禁用)、标题(50字)、帖子内容(1000字)、帖子图片地址列表(8张)
        PostType postType = postTypeMapper.selectOne(new QueryWrapper<PostType>()
                .eq("type_name", postTypeName)
                .eq("status", 1));
        if (postType == null) {
            return Result.fail(MessageConstant.POST_TYPE_NO_EXIST_OR_BAN);
        }
        if (!(title.length() > 0 && title.length() <= 50)) {
            return Result.fail(MessageConstant.TITLE_TOO_LONG);
        }
        if (!(content.length() > 0 && content.length() <= 1000)) {
            return Result.fail(MessageConstant.CONTENT_TOO_LONG);
        }
        if (picUrlList.size() > 8) {
            return Result.fail(MessageConstant.PIC_LIMIT);
        }
        // 拷贝属性
        Post post = BeanUtil.copyProperties(postDTO, Post.class);
        post.setPostTypeId(Math.toIntExact(postType.getId()));
        // 获取该用户的ip属地
        try {
            Searcher searcher = ip2RegionUtils.getSearcher();
            String ipAddress = searcher.search(ip);    //中国|0|江苏省|泰州市|移动
            String[] split = ipAddress.split("\\|");
            post.setIpRegion(split[2]);
        } catch (Exception e) {
            post.setIpRegion("未知");
        }

        // 将PostDTO拷贝到Post中，再补充其它字段
//        ip_region, title, content, pic_url_list,
//        post_type_id, user_id, username, user_avatar
        post.setUsername(user.getUsername());
        post.setUserId(user.getId());
        post.setUserAvatar(user.getAvatar());
        // 向帖子表插入该条数据
        postMapper.insert(post);

        // 将该帖子信息放入该用户粉丝的收件箱中
        List<Follow> follows = followMapper.selectList(new QueryWrapper<Follow>().eq("follow_user_id", user.getId()));
        ArrayList<Long> fansIds = new ArrayList<>();
        follows.forEach(f->{
            fansIds.add(f.getUserId());
        });
        long nowTimeStamp = new Date().getTime();
        fansIds.forEach(id->{
            stringRedisTemplate.opsForZSet().add(RedisConstants.USER_INBOX+id,"postId:"+post.getId(),nowTimeStamp);
        });
        return Result.ok();
    }

    @Override
    public Result<List<PostCardVO>> getPostByType(String postType, String minTime, Integer offset) {

        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Post> topPostQuery = new QueryWrapper<Post>().eq("is_top", 1);
        // 将minTime转为Date类型方便查数据库
        long time = Long.parseLong(minTime);
        Date date = new Date(time);
        if (!("全部".equals(postType))) {
            List<PostType> postTypes = postTypeMapper.selectList(new QueryWrapper<PostType>()
                    .eq("status", 1)
                    .eq("type_name", postType));
            if (postTypes.size() == 0) {
                return Result.fail("请选择正确的板块");
            }
            postQueryWrapper.eq("post_type_id", postTypes.get(0).getId());
            topPostQuery.eq("post_type_id", postTypes.get(0).getId());
        }
        postQueryWrapper
                .lt("create_time", date)
                .eq("is_top", 0)
                .orderByDesc("create_time");
        // 当为第一次查询的时候，先查询置顶帖子
        List<Post> posts = new ArrayList<>();
        if (offset == 1) {
            posts.addAll(postMapper.selectList(topPostQuery));
        }
        // 封装分页对象
        Page<Post> page = new Page<>(offset, MessageConstant.USER_PAGE_SIZE);
        Page<Post> postPage = postMapper.selectPage(page, postQueryWrapper);
        ArrayList<PostCardVO> postCardVOList = new ArrayList<>();
        posts.addAll(postPage.getRecords());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Post post : posts) {
            PostCardVO postCardVO = BeanUtil.copyProperties(post, PostCardVO.class);
            // 格式化时间
            postCardVO.setLcTime(dateFormat.format(post.getLcTime()));
//            example.jpg?x-oss-process=image/resize,m_fill,h_100,w_100/quality,q_80
            List<String> picUrlList = postCardVO.getPicUrlList();
            ArrayList<String> newPic = new ArrayList<>();
            // 图片压缩
            postCardVO.setUserAvatar(post.getUserAvatar()+BusinessConstant.OSS_RESIZE_URL_EXTEND);
            picUrlList.forEach(pic -> {
                pic += BusinessConstant.OSS_RESIZE_URL_EXTEND;
                newPic.add(pic);
            });
            postCardVO.setPicUrlList(newPic);
            postCardVOList.add(postCardVO);
        }
        return Result.ok(postCardVOList);
    }

    @Override
    public Result<PostVO> postDetail(Integer postId) {
        // 获取该帖子信息
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return Result.fail(MessageConstant.POST_NOT_EXIST);
        }
        // 增加浏览量-后续在定时任务中将该帖子的浏览量持久化到数据库，然后删除该key，直到第二次访问继续时创建
        stringRedisTemplate.opsForValue().increment(RedisConstants.POST_VIEW_COUNT + postId);
        // 增加相应的浏览量分数
        HotUtils.addPostHot(postId,RedisConstants.VIEW_SCORE,favoritesMapper,postMapper,stringRedisTemplate);

        // 拷贝post属性到postVO
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        PostVO postVO = BeanUtil.copyProperties(post, PostVO.class);
        postVO.setAvatar(post.getUserAvatar());
        postVO.setPostType(postTypeMapper.selectById(post.getPostTypeId()).getTypeName());
        // 格式化时间
        postVO.setCreateTime(dateFormat.format(post.getCreateTime()));
//        picUrlList   String -> list


        // 默认为false
        postVO.setIsFavorite(false);

        LoginUserDTO user = UserHolder.getUser();
        if (user != null) {
            // 获取当前用户是否收藏
            List<Favorites> favorites = favoritesMapper.selectList(new QueryWrapper<Favorites>()
                    .eq("user_id", user.getId())
                    .eq("post_id", postId));
            if (favorites.size() != 0) {
                // 该用户收藏过
                postVO.setIsFavorite(true);
            }
        }
        // 将postVO中的图片进行压缩
        List<String> picUrlList = postVO.getPicUrlList();
        ArrayList<String> newPic = new ArrayList<>();
        picUrlList.forEach(pic -> {
            pic += BusinessConstant.OSS_60Q_URL_EXTEND;
            newPic.add(pic);
        });
        postVO.setPicUrlList(newPic);
        return Result.ok(postVO);
    }

    @Override
    public Result favorite(Integer postId) {
        LoginUserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        QueryWrapper<Favorites> favoritesQueryWrapper = new QueryWrapper<>();
        favoritesQueryWrapper
                .eq("user_id", user.getId())
                .eq("post_id", postId);
        Favorites favorites = favoritesMapper.selectOne(favoritesQueryWrapper);
        if (favorites != null) {
            return Result.ok("收藏成功");
        }
        favorites = new Favorites();
        favorites.setPostId(Long.valueOf(postId));
        favorites.setUserId(user.getId());
        favoritesMapper.insert(favorites);
        // 更新帖子分数-收藏
        HotUtils.addPostHot(postId,RedisConstants.FAVORITE_SCORE,favoritesMapper,postMapper,stringRedisTemplate);
        return Result.ok("收藏成功");
    }

    @Override
    public Result unFavorite(Integer postId) {
        LoginUserDTO user = UserHolder.getUser();
        if (user == null) {
            return Result.fail("请先登录");
        }
        // 直接删除
        QueryWrapper<Favorites> favoritesQueryWrapper = new QueryWrapper<>();
        favoritesQueryWrapper
                .eq("user_id", user.getId())
                .eq("post_id", postId);
        favoritesMapper.delete(favoritesQueryWrapper);
        // 更新帖子分数-取消收藏
        HotUtils.addPostHot(postId,-RedisConstants.FAVORITE_SCORE,favoritesMapper,postMapper,stringRedisTemplate);
        return Result.ok("已取消收藏");
    }

    @Override
    public Result<List<PostCardVO>> random() {
        // 获得该用户自己的推荐，每登录怎么办
        LoginUserDTO user = UserHolder.getUser();
        List<String> removeKey = new ArrayList<>();
        if(user == null){
            // 用户没有登录，使用默认的随机推荐
            // random:default
            Long defaultSize = stringRedisTemplate.opsForZSet().size(RedisConstants.DEFAULT_RANDOM);
            if(defaultSize == null || defaultSize < 5){
                // 默认的推荐集合不足5条，进行拷贝
                ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
                Long size = zSetOps.size(RedisConstants.HOT_POST_KEY); // 获取原始 ZSET 的大小
                if (size != null && size > 0) {
                    Set<ZSetOperations.TypedTuple<String>> tuples = zSetOps.rangeWithScores(RedisConstants.HOT_POST_KEY, 0, -1);
                    zSetOps.add(RedisConstants.DEFAULT_RANDOM, tuples); // 批量插入到目标 ZSET 中
                }
            }
            // 从默认推荐表中随机获取5条数据
            removeKey = stringRedisTemplate.opsForZSet().randomMembers(RedisConstants.DEFAULT_RANDOM, MessageConstant.USER_PAGE_SIZE);
            removeKey.forEach(r->{
                stringRedisTemplate.opsForZSet().remove(RedisConstants.DEFAULT_RANDOM,r);
            });
        }else{
            // random:user:
            Long userSize = stringRedisTemplate.opsForZSet().size(RedisConstants.USER_RANDOM + user.getId());
            if(userSize == null || userSize < 5){
                // 用户推荐集合中元素不足5条，进行拷贝
                ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
                Long size = zSetOps.size(RedisConstants.HOT_POST_KEY); // 获取原始 ZSET 的大小
                if (size != null && size > 0) {
                    Set<ZSetOperations.TypedTuple<String>> tuples = zSetOps.rangeWithScores(RedisConstants.HOT_POST_KEY, 0, -1);
                    zSetOps.add(RedisConstants.USER_RANDOM+user.getId(), tuples); // 批量插入到目标 ZSET 中
                    // 设置过期时间，保证数据更新。
                    stringRedisTemplate.expire(RedisConstants.USER_RANDOM+user.getId(),RedisConstants.RANDOM_TTL, TimeUnit.DAYS);
                }
            }
            // 从用户推荐表中随机获取5条数据
            removeKey = stringRedisTemplate.opsForZSet().randomMembers(RedisConstants.USER_RANDOM+user.getId(), MessageConstant.USER_PAGE_SIZE);
            removeKey.forEach(r->{
                stringRedisTemplate.opsForZSet().remove(RedisConstants.USER_RANDOM+user.getId(),r);
            });
        }
        // 查询removeKey中的帖子信息
        List<Post> posts = postMapper.selectBatchIds(removeKey);
        ArrayList<PostCardVO> postCardVOList = new ArrayList<>();
        // 对帖子进行PostCardVO封装
        for (Post post : posts) {
            PostCardVO postCardVO = BeanUtil.copyProperties(post, PostCardVO.class);
//            example.jpg?x-oss-process=image/resize,m_fill,h_100,w_100/quality,q_80
            List<String> picUrlList = postCardVO.getPicUrlList();
            // 进行图片压缩
            postCardVO.setUserAvatar(post.getUserAvatar()+BusinessConstant.OSS_RESIZE_URL_EXTEND);
            ArrayList<String> newPic = new ArrayList<>();
            picUrlList.forEach(pic -> {
                pic += BusinessConstant.OSS_RESIZE_URL_EXTEND;
                newPic.add(pic);
            });
            postCardVO.setPicUrlList(newPic);
            postCardVOList.add(postCardVO);
        }
//        stringRedisTemplate.opsForZSet()
        return Result.ok(postCardVOList);
    }


}




