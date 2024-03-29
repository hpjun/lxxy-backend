package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.model.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yhp
 * @date 2024/3/28 17:29
 */

@RestController("adminHomeController")
@RequestMapping("/admin/home")
@Api(tags = "主页接口")
@Slf4j
public class HomeController {

    @GetMapping()
    @ApiOperation("获取主页数据")
    public Result getHomeData(){
        // TODO 获取主页数据
        return Result.ok("获取主页数据HomeVO");
    }

    @GetMapping("/is-login")
    @ApiOperation("验证是否登录")
    public Result isLogin(){
        return Result.ok();
    }
}
