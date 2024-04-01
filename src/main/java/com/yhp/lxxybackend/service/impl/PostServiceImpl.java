package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.exception.BusinessException;
import com.yhp.lxxybackend.mapper.PostCommentMapper;
import com.yhp.lxxybackend.mapper.PostTypeMapper;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.Post;
import com.yhp.lxxybackend.model.entity.PostComment;
import com.yhp.lxxybackend.model.entity.PostType;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.service.PostCommentService;
import com.yhp.lxxybackend.service.PostService;
import com.yhp.lxxybackend.mapper.PostMapper;
import com.yhp.lxxybackend.utils.Ip2RegionUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
                    .eq("post_id",id));
        }
        return Result.ok("删除成功");
    }

    @Override
    public Result changeTop(Integer postId) {
        // 查询该帖子信息
        Post post = postMapper.selectById(postId);
        if(post == null){
            return Result.fail("该帖子不存在");
        }
        Integer isTop = post.getIsTop();
        if(isTop == 1){
            // 取消置顶
            post.setIsTop(0);
            postMapper.updateById(post);
            return Result.ok(false);
        }else{
            // 置顶
            post.setIsTop(1);
            postMapper.updateById(post);
            return Result.ok(true);
        }
    }

    @Override
    public Result publish(PostDTO postDTO,String ip) {
        String postTypeName = postDTO.getPostType();
        String title = postDTO.getTitle();
        String content = postDTO.getContent();
        List<String> picUrlList = postDTO.getPicUrlList();
        // 校验信息 帖子板块(是否存在，是否被禁用)、标题(50字)、帖子内容(1000字)、帖子图片地址列表(8张)
        PostType postType = postTypeMapper.selectOne(new QueryWrapper<PostType>()
                .eq("type_name", postTypeName)
                .eq("status",1));
        if(postType == null){
            return Result.fail(MessageConstant.POST_TYPE_NO_EXIST_OR_BAN);
        }
        if(title.length()>50){
            return Result.fail(MessageConstant.TITLE_TOO_LONG);
        }
        if(content.length()>1000){
            return Result.fail(MessageConstant.CONTENT_TOO_LONG);
        }
        if(picUrlList.size()>8){
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
            throw new BusinessException("解析ip出错");
        }
        // 获取当前登录用户信息，将信息封装到Post中
        LoginUserDTO user = UserHolder.getUser();
        // 将PostDTO拷贝到Post中，再补充其它字段
//        ip_region, title, content, pic_url_list,
//        post_type_id, user_id, username, user_avatar
        post.setUsername(user.getUsername());
        post.setUserId(user.getId());
        post.setUserAvatar(user.getAvatar());
        // 向帖子表插入该条数据
        postMapper.insert(post);

        return Result.ok();
    }
}




