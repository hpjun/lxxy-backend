package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author yhp
 * @date 2024/3/28 19:56
 */

@RestController("adminPostTypeController")
@RequestMapping("/admin/postType")
@Api(tags = "帖子板块接口")
@Slf4j
public class PostTypeController {

    @GetMapping("/list")
    @ApiOperation("分页获取板块信息")
    public Result list(@RequestParam Integer pageNum){
        // TODO 分页获取板块信息
        return Result.ok("分页获取板块信息"+pageNum);
    }

    @PostMapping()
    @ApiOperation("新增板块信息")
    public Result add(@RequestParam String typeName){
        // TODO 新增板块信息
        return Result.ok("新增板块信息"+typeName);
    }

    @PutMapping("/{postTypeId}")
    @ApiOperation("修改板块信息")
    public Result edit(@PathVariable("postTypeId") Integer postTypeId,
                       @RequestParam String typeName){
        // TODO 修改板块信息
        return Result.ok("修改板块信息"+postTypeId+typeName);
    }

    @PutMapping("/status/{postTypeId}")
    @ApiOperation("禁用/启用板块")
    public Result changeStatus(@PathVariable("postTypeId") Integer postTypeId){
        // TODO 禁用/启用板块，
        return Result.ok("禁用/启用板块"+postTypeId);
    }

}
