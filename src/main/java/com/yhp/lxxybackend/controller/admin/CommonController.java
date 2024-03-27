package com.yhp.lxxybackend.controller.admin;

import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.UploadTypeConstant;
import com.yhp.lxxybackend.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


import com.yhp.lxxybackend.model.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
@Api(tags="通用接口")
public class CommonController {

    @Resource
    AliOssUtil aliOssUtil;


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
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
}
