package com.yhp.lxxybackend.controller.user;


import com.sun.xml.internal.bind.v2.TODO;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.model.dto.PostDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.dto.UserDTO;
import com.yhp.lxxybackend.model.dto.UserFormDTO;
import com.yhp.lxxybackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController("userUserController")
@RequestMapping("/user/user")
@Api(tags = "C端-用户管理接口")
@Slf4j
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/login")
    @ApiOperation("登录功能")
    public Result login(@RequestBody UserFormDTO userFormDTO){
        return userService.login(userFormDTO,"user");
    }

    @PostMapping("/register")
    @ApiOperation("注册功能")
    public Result register(@RequestBody UserFormDTO userFormDTO){
        return userService.register(userFormDTO);
    }

    @GetMapping("/user-info")
    @ApiOperation("获取主页用户信息")
    public Result userInfo(){
        // TODO 获取主页用户信息
        return Result.ok("获取主页用户信息");
    }

    @GetMapping("/{userId}")
    @ApiOperation("获取用户详细信息")
    public Result Detail(@PathVariable("userId") Integer userId){
//        TODO 获取用户详细信息，不包含敏感信息
        return Result.ok("获取用户详细信息"+userId);
    }

    @GetMapping("/logout")
    @ApiOperation("退出当前登录用户")
    public Result logout(){
        // TODO 退出当前登录用户
        return Result.ok("退出当前登录用户");
    }

    @PutMapping()
    @ApiOperation("编辑当前用户资料")
    public Result edit(@RequestBody UserDTO userDTO){
        // TODO 编辑当前用户资料
        return Result.ok("编辑当前用户资料"+userDTO);
    }

    @GetMapping("/dynamic")
    @ApiOperation("获取主页动态-前端未完成")
    public Result dynamic(@RequestParam String minTime,
                          @RequestParam(required = false, defaultValue = "0") Integer offset){
        // TODO 获取当前用户主页动态,minTime应该为zset的score，默认是当前前端第一次请求的时间戳，之后后端都会返回给前端
        return Result.ok("获取当前用户主页动态"+minTime+offset);
    }

    @GetMapping("/favorites")
    @ApiOperation("获取主页收藏夹")
    public Result favorites(@RequestParam String pageNum){
        // TODO 获取主页收藏夹
        return Result.ok("获取主页收藏夹"+pageNum+ MessageConstant.USER_PAGE_SIZE);
    }

    @GetMapping("/fans")
    @ApiOperation("获取主页粉丝列表")
    public Result fans(@RequestParam String pageNum){
        // TODO 获取主页粉丝列表
        return Result.ok("获取主页粉丝列表"+pageNum+MessageConstant.USER_PAGE_SIZE+5);
    }

    @GetMapping("/follows")
    @ApiOperation("获取主页关注列表")
    public Result follows(@RequestParam String pageNum){
        // TODO 获取主页关注列表
        return  Result.ok("获取主页关注列表"+pageNum+MessageConstant.USER_PAGE_SIZE+5);
    }

    @GetMapping("/get-phone")
    @ApiOperation("获取用户手机号")
    public Result getPhone(){
        // TODO 获取当前登录用户手机号
        return Result.ok("获取当前登录用户手机号");
    }

    @GetMapping("/other/{userId}")
    @ApiOperation("查看别人信息-前端未完成")
    public Result showOtherUserInfo(@PathVariable("userId") Integer userId){
        // TODO 查看别人信息
        return Result.ok("查看别人信息"+userId);
    }

    @PostMapping("/{followUserId}")
    @ApiOperation("关注-前端未完成")
    public Result follow(@PathVariable("followUserId") Integer followUserId){
        // TODO 关注
        return Result.ok("关注"+followUserId);
    }

    @PostMapping("/un-follow/{followUserId}")
    @ApiOperation("取消关注-前端未完成")
    public Result unFollow(@PathVariable("followUserId") Integer followUserId){
        // TODO 取消关注
        return Result.ok("取消关注"+followUserId);
    }



}
