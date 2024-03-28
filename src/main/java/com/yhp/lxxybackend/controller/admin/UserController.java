package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.dto.UserDTO;
import com.yhp.lxxybackend.model.dto.UserFormDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yhp
 * @date 2024/3/28 17:29
 */

@RestController("adminUserController")
@RequestMapping("/admin/user")
@Api(tags = "用户接口")
@Slf4j
public class UserController {

    @PostMapping("/login")
    @ApiOperation("登录")
    public Result login(@RequestBody UserFormDTO userFormDTO){
        // TODO 管理员登录
        return Result.ok("管理员登录+token"+userFormDTO);
    }

    @PutMapping("/update-pwd")
    @ApiOperation("修改密码")
    public Result updatePwd(@RequestBody UserFormDTO userFormDTO){
        // TODO 修改密码
        return Result.ok("修改密码"+userFormDTO);
    }

    @GetMapping("/logout")
    @ApiOperation("退出登录")
    public Result logout(){
        // TODO 退出登录
        return Result.ok("退出登录");
    }

    @GetMapping("/list")
    @ApiOperation("分页查询用户信息")
    public Result list(@RequestParam Integer pageNum,
                       @RequestParam String sc,
                       @RequestParam String ban){
        // TODO 分页查询用户信息
        return Result.ok("分页查询用户信息"+pageNum+sc+ban);
    }

    @PutMapping("/{userId}")
    @ApiOperation("修改用户信息-前端未完成")
    public Result edit(@PathVariable("userId") Integer userId,
                       @RequestBody UserDTO userDTO){
        // TODO 修改用户信息
        return Result.ok("修改用户信息"+userId+userDTO);
    }

    @GetMapping("/{userId}")
    @ApiOperation("查看用户详细信息")
    public Result userDetail(@PathVariable("userId") Integer userId){
        // TODO 查看用户详细信息
        return Result.ok("查看用户详细信息"+userId);
    }

    @DeleteMapping("/delete")
    @ApiOperation("批量删除用户")
    public Result delete(@RequestParam List<Integer> ids){
        // TODO 批量删除用户
        return Result.ok("批量删除用户"+ids);
    }

    @PutMapping("/status/{userId}")
    @ApiOperation("封禁/解封用户")
    public Result changeStatus(@PathVariable("userId") Integer userId){
        // TODO 封禁/解封用户
        return Result.ok("封禁/解封用户"+userId);
    }


}
