package com.yhp.lxxybackend.config;

import com.yhp.lxxybackend.interceptor.AdminAuthInterceptor;
import com.yhp.lxxybackend.interceptor.RefreshTokenInterceptor;
import com.yhp.lxxybackend.interceptor.UserLoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        log.info("开始注册自定义拦截器...");
//        registry.addInterceptor(new AdminAuthInterceptor())
//                .addPathPatterns("/admin/**")
//                .excludePathPatterns(
//                        "/admin/login",
//                        "/admin/code",
//                        "/blog/hot",
//                        "/shop/**",
//                        "/shop-type/**",
//                        "/voucher/**"
//                ).order(1);
//        registry.addInterceptor(new UserLoginInterceptor())
//                .addPathPatterns("/user/**")
//                .excludePathPatterns(
//                        "/user/user/login",
//                        "/user/user/code",
//                        "/user/activity/all",
//                        "/user/post/random/next",
//                        "/user/post/latest",
//
//
//
//                        "/voucher/**"
//                ).order(2);
//        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
//                .addPathPatterns("/**").order(0);
    }
}
