package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.UploadTypeConstant;
import com.yhp.lxxybackend.service.CommonService;
import com.yhp.lxxybackend.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import com.yhp.lxxybackend.model.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

@RestController("adminCommonController")
@RequestMapping("/admin/common")
@Slf4j
@Api(tags="通用接口")
public class CommonController {

    @Resource
    CommonService commonService;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        return commonService.upload(file);
    }

    @PostMapping("/code/{phone}")
    @ApiOperation("发送验证码")
    public Result sendCode(@PathVariable(required = false, value = "phone") String phone) {
        return commonService.sendCode(phone);
    }
}
