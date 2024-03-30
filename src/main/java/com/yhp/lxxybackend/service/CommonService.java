package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import org.springframework.web.multipart.MultipartFile;

public interface CommonService {




    Result<String> upload(MultipartFile file);

    Result sendCode(String phone);
}
