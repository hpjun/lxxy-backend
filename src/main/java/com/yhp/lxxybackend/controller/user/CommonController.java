package com.yhp.lxxybackend.controller.user;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author yhp
 * @date 2024/3/28 14:50
 */

@RestController("userCommonController")
@RequestMapping("/user/common")
@Api(tags = "C端-通用接口")
@Slf4j
public class CommonController {

    @Resource
    CommonService commonService;

    @PostMapping("/code/{phone}")
    @ApiOperation("发送验证码")
    public Result sendCode(@PathVariable(required = false, value = "phone") String phone) {
        return commonService.sendCode(phone);
    }

    @PostMapping("/upload")
    @ApiOperation("上传图片")
    public Result upload(MultipartFile file) {
        return commonService.upload(file);
    }
}
