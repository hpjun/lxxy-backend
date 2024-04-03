package com.yhp.lxxybackend.config;

import com.yhp.lxxybackend.utils.AliOssUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "lxxy.alioss")
@Data
@Configuration
@Slf4j
public class OssConfig {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private Boolean cname;

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil() {
        log.info("开始创建阿里云文件上传工具类对象：");
        return new AliOssUtil(endpoint,
                accessKeyId,
                accessKeySecret,
                bucketName,
                cname);
    }
}
