package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {
    /**
     * 上传图片
     * @param file
     * @return
     */
    Result<String> upload(MultipartFile file);

    /**
     * 发送验证码
     * @param phone
     * @return
     */
    Result sendCode(String phone);
}
