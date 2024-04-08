package com.yhp.lxxybackend.controller.user;


import com.yhp.lxxybackend.model.dto.*;
import com.yhp.lxxybackend.model.vo.PostCardVO;
import com.yhp.lxxybackend.model.vo.UserVO;
import com.yhp.lxxybackend.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController("userUserController")
@RequestMapping("/user/user")
@Api(tags = "C端-用户管理接口")
@Slf4j
public class UserController {

    @Resource
    UserService userService;

    @PostMapping("/login")
    @ApiOperation("登录功能")
    public Result login(@RequestBody UserFormDTO userFormDTO) {
        return userService.login(userFormDTO, "user");
    }

    @PostMapping("/register")
    @ApiOperation("注册功能")
    public Result register(@RequestBody UserFormDTO userFormDTO) {
        return userService.register(userFormDTO);
    }

    @GetMapping("/user-info")
    @ApiOperation("获取主页用户信息")
    public Result<UserVO> userInfo() {
        return userService.userInfo();
    }

    @GetMapping()
    @ApiOperation("获取我的详细信息")
    public Result<UserDTO> detail() {
        return userService.detail();
    }

    @GetMapping("/logout")
    @ApiOperation("退出当前登录用户")
    public Result logout() {
        return userService.logout();
    }

    @PutMapping()
    @ApiOperation("编辑当前用户资料")
    public Result<UserDTO> edit(@RequestBody UserDTO userDTO) {
        return userService.edit(userDTO);
    }

    @GetMapping("/dynamic")
    @ApiOperation("获取主页动态-前端未完成")
    public Result<List<Object>> dynamic(@RequestParam String minTime,
                                        @RequestParam Integer offset,
                                        @RequestParam(required = false, defaultValue = "") Long userId) {
        // 从Redis获取该用户的收件箱
        return userService.dynamic(minTime, offset, userId);
    }

    @GetMapping("/favorites")
    @ApiOperation("获取主页收藏夹")
    public Result<List<PostCardVO>> favorites(@RequestParam Integer pageNum) {
        return userService.favorites(pageNum);
    }

    @GetMapping("/fans")
    @ApiOperation("获取主页粉丝列表")
    public Result<List<UserCardDTO>> fans(@RequestParam Integer pageNum,
                                          @RequestParam(required = false, defaultValue = "") Long userId) {
        return userService.fans(pageNum,userId);
    }

    @GetMapping("/follows")
    @ApiOperation("获取主页关注列表")
    public Result<List<UserCardDTO>> follows(@RequestParam Integer pageNum,
                                             @RequestParam(required = false, defaultValue = "") Long userId) {
        return userService.follows(pageNum,userId);
    }

    @GetMapping("/get-phone")
    @ApiOperation("获取用户手机号")
    public Result<String> getPhone() {
        return userService.getPhone();
    }

    @GetMapping("/other/{userId}")
    @ApiOperation("查看别人信息")
    public Result<UserVO> showOtherUserInfo(@PathVariable("userId") Integer userId) {
        return userService.otherUserInfo(userId);
    }

    @PostMapping("/{followUserId}")
    @ApiOperation("关注-前端未完成")
    public Result follow(@PathVariable("followUserId") Integer followUserId) {
        return userService.follow(followUserId);
    }

    @PostMapping("/un-follow/{followUserId}")
    @ApiOperation("取消关注-前端未完成")
    public Result unFollow(@PathVariable("followUserId") Integer followUserId) {
        return userService.unFollow(followUserId);
    }
}
