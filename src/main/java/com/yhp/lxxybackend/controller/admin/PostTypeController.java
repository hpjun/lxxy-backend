package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.vo.PostTypeVO;
import com.yhp.lxxybackend.service.PostTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.runtime.JSType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 19:56
 */

@RestController("adminPostTypeController")
@RequestMapping("/admin/postType")
@Api(tags = "帖子板块接口")
@Slf4j
public class PostTypeController {

    @Resource
    PostTypeService postTypeService;

    @GetMapping("/list")
    @ApiOperation("分页获取板块信息")
    public Result<List<PostTypeVO>> list(@RequestParam Integer pageNum){
        return postTypeService.listPostType(pageNum);
    }


    @GetMapping()
    @ApiOperation("查询所有板块名称")
    public Result<List<String>> all(){
        return postTypeService.all();
    }


    @PostMapping()
    @ApiOperation("新增板块信息")
    public Result add(@RequestParam String typeName){
        return postTypeService.add(typeName);
    }

    @PutMapping("/{postTypeId}")
    @ApiOperation("修改板块信息")
    public Result edit(@PathVariable("postTypeId") Integer postTypeId,
                       @RequestParam String typeName){
        return postTypeService.edit(postTypeId,typeName);
    }

    @PutMapping("/status/{postTypeId}")
    @ApiOperation("禁用/启用板块")
    public Result changeStatus(@PathVariable("postTypeId") Integer postTypeId){
        return postTypeService.changeStatus(postTypeId);
    }

}
