package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.service.CommonService;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.utils.AliOssUtil;
import com.yhp.lxxybackend.utils.RegexUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.ThumbnailatorUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
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
        log.info("文件上传：{}", file);
        try {
            //TODO 对上传的文件做类型判断、压缩、转格式、确认大小符合要求之后才上传
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            String extend = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            if (!(ThumbnailatorUtils.isSupportedOutputFormat(extend))) {
                return Result.fail("上传文件格式不正确");
            }
            String objectName = UUID.randomUUID() + extend;
            //文件的请求路径
            String fileName = aliOssUtil.upload(compressImage(file), objectName);
            return Result.ok(fileName);
        } catch (IOException e) {
            log.error("文件上传失败:{}", e);
        }

        return Result.fail(MessageConstant.UPLOAD_FAILED);
    }

    private byte[] compressImage(MultipartFile file) throws IOException {
        // 进行图片压缩处理
        double outputQuality = 0.8;
        byte[] imageData = file.getBytes(); // 获取原始图片数据
        // 循环压缩图片，直到图片大小小于1MB
        if (file.getSize() < 1024 * 1024) {
            outputQuality = 1.0; // 初始压缩质量
        }else{
            outputQuality = 0.8;
        }

        while (true) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // 使用 Thumbnails 进行图片压缩
            Thumbnails.of(inputStream)
                    .outputQuality(outputQuality)
                    .outputFormat("jpg") // 或其他格式
                    .scale(1D)
                    .toOutputStream(outputStream);

            // 获取压缩后的图片数据
            byte[] compressedImageData = outputStream.toByteArray();

            // 检查压缩后的图片大小是否小于1MB
            if (compressedImageData.length < 1024 * 1024) {
                // 如果小于1MB，则返回压缩后的图片数据
                return compressedImageData;
            }
            // 将压缩的数据放入原始数据
            imageData = compressedImageData;
            // 降低压缩质量，准备下一次压缩
            outputQuality = 0.8; // 可根据实际情况调整压缩质量的变化幅度
        }
    }

    @Override
    public Result sendCode(String phone) {
        if (StrUtil.isBlank(phone) || "undefined".equals(phone)) {
            // 没有手机号，获取当前登录的手机号
            LoginUserDTO user = UserHolder.getUser();
            if (user == null) {
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
        if (!StrUtil.isBlank(s)) {
            return Result.ok("已发送验证码");
        }

        // 符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 保存验证码到Redis
        stringRedisTemplate.opsForValue().set(RedisConstants.LOGIN_CODE_KEY + phone, code, RedisConstants.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 发送验证码
        log.debug("发送短信验证码成功：{}", code);

        return Result.ok("已发送验证码");
    }
}
