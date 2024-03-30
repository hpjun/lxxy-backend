package com.yhp.lxxybackend.controller.user;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.UploadTypeConstant;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.service.CommonService;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.utils.AliOssUtil;
import com.yhp.lxxybackend.utils.RegexUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import com.yhp.lxxybackend.constant.RedisConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
