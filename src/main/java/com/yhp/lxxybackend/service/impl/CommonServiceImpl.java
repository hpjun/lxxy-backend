package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.constant.UploadTypeConstant;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.service.CommonService;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.utils.AliOssUtil;
import com.yhp.lxxybackend.utils.RegexUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author yhp
 * @date 2024/3/30 14:07
 */

@Service
@Slf4j
public class CommonServiceImpl implements CommonService {

    @Resource
    AliOssUtil aliOssUtil;
    @Resource
    UserService userService;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}",file);
        try {
            //TODO 对上传的文件做类型判断、压缩、转格式、确认大小符合要求之后才上传


            //原始文件名
            String originalFilename = file.getOriginalFilename();
            String extend = originalFilename.substring(originalFilename.lastIndexOf('.')).toLowerCase();
            if(!(UploadTypeConstant.ALLOW_TYPE.contains(extend))){
                return Result.fail("上传文件格式不正确");
            }

            String objectName = UUID.randomUUID().toString()+extend;

            //文件的请求路径
            String fileName = aliOssUtil.upload(file.getBytes(), objectName);
            return com.yhp.lxxybackend.model.dto.Result.ok(fileName);
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
        }

        return Result.fail(MessageConstant.UPLOAD_FAILED);
    }

    @Override
    public Result sendCode(String phone) {
        if(StrUtil.isBlank(phone) || "undefined".equals(phone)){
            // 没有手机号，获取当前登录的手机号
            LoginUserDTO user = UserHolder.getUser();
            if(user == null){
                // 没有登录，不能调用这个接口
                return Result.fail("未登录，不能调用此接口");
            }
            // 获得当前登录用户的手机号
            phone = userService.getById(user.getId()).getPhone();
        }
        // 校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }

        // 检查Redis中是否有验证码
        String s = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if(!StrUtil.isBlank(s)){
            return Result.ok("已发送验证码");
        }

        // 符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 保存验证码到Redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 发送验证码
        log.debug("发送短信验证码成功：{}",code);

        return Result.ok("已发送验证码");
    }
}
