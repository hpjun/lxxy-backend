package com.yhp.lxxybackend.controller.user;


import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Api(tags = "用户管理模块")
public class UserController {

    @GetMapping("/login")
    @ApiOperation("登录功能")
    public Result login(){
        return null;
    }
}
