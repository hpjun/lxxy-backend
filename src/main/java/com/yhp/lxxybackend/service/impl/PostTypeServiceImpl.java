package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.entity.PostType;
import com.yhp.lxxybackend.model.vo.PostTypeVO;
import com.yhp.lxxybackend.service.PostTypeService;
import com.yhp.lxxybackend.mapper.PostTypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
* @author Admin
* @description 针对表【post_type(帖子板块)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class PostTypeServiceImpl extends ServiceImpl<PostTypeMapper, PostType>
    implements PostTypeService{

    @Resource
    PostTypeMapper postTypeMapper;

    @Override
    public Result<List<PostTypeVO>> listPostType(Integer pageNum) {

        QueryWrapper<PostType> postTypeQueryWrapper = new QueryWrapper<>();
        // 封装分页对象
        Page<PostType> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        // 分页查询
        Page<PostType> postTypePage = postTypeMapper.selectPage(page, postTypeQueryWrapper);
        List<PostType> postTypes = postTypePage.getRecords();
        ArrayList<PostTypeVO> postTypeVOList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (PostType postType : postTypes) {
            PostTypeVO postTypeVO = new PostTypeVO();
            postTypeVO.setId(Math.toIntExact(postType.getId()));
            postTypeVO.setTypeName(postType.getTypeName());
            postTypeVO.setStatus(postType.getStatus() == 1);
            postTypeVO.setUpdateTime(dateFormat.format(postType.getUpdateTime()));
            postTypeVOList.add(postTypeVO);
        }
        return Result.ok(postTypeVOList,postTypePage.getTotal());
    }

    @Override
    public Result add(String typeName) {
        List<PostType> postTypes = postTypeMapper.selectList(new QueryWrapper<PostType>().eq("type_name", typeName));
        if(postTypes.size() == 1){
            // type_name
            return Result.fail(MessageConstant.POST_TYPE_EXIST);
        }
        PostType postType = new PostType();
        postType.setTypeName(typeName);
        postTypeMapper.insert(postType);
        return Result.ok();
    }

    @Override
    public Result edit(Integer postTypeId, String typeName) {
        List<PostType> postTypes = postTypeMapper.selectList(new QueryWrapper<PostType>().eq("type_name", typeName));
        if(postTypes.size() == 1){
            // type_name
            return Result.fail(MessageConstant.POST_TYPE_EXIST);
        }
        PostType postType = new PostType();
        postType.setTypeName(typeName);
        postType.setId(Long.valueOf(postTypeId));
        postTypeMapper.updateById(postType);
        return Result.ok();
    }

    @Override
    public Result changeStatus(Integer postTypeId) {
        // 查询该板块是否存在
        PostType postType = postTypeMapper.selectById(postTypeId);
        if(postType == null){
            return Result.fail("该板块不存在");
        }
        Integer status = postType.getStatus();
        if(status == 1){
            // 禁用
            postType.setStatus(0);
            postTypeMapper.updateById(postType);
            return Result.ok(false);
        }else{
            // 启用
            postType.setStatus(1);
            postTypeMapper.updateById(postType);
            return Result.ok(true);
        }
    }
}




