package com.yhp.lxxybackend.config;

import com.yhp.lxxybackend.interceptor.AdminAuthInterceptor;
import com.yhp.lxxybackend.interceptor.RefreshTokenInterceptor;
import com.yhp.lxxybackend.interceptor.UserLoginInterceptor;
import com.yhp.lxxybackend.utils.Ip2RegionUtils;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.io.IOException;

@Configuration
@Slf4j
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(new AdminAuthInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/user/login",
                        "/admin/common/code/*"
                ).order(1);
        registry.addInterceptor(new UserLoginInterceptor(stringRedisTemplate))
                .addPathPatterns("/user/**")
                .excludePathPatterns(
                        "/user/user/login",
                        "/user/user/register",
                        "/user/common/code/*",

                        "/user/activity/all",
                        "/user/post/random/next",
                        "/user/post/latest",
                        "/user/post/*",
                        "/user/comment/*"
                ).order(2);
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
                .addPathPatterns("/**").order(0);
    }


    /**
     * 跨越配置
     * 改用过滤器CorsFilter 来配置跨域，由于Filter的位置是在Interceptor之前的，问题得到解决：
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 设置允许跨域请求的域名
        config.addAllowedOrigin("*");
        // 是否允许证书 不再默认开启
        // config.setAllowCredentials(true);
        // 设置允许的方法
        config.addAllowedMethod("*");
        // 允许任何头
        config.addAllowedHeader("*");
        config.addExposedHeader("token");
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        return new CorsFilter(configSource);
    }



    @Bean
    @ConditionalOnMissingBean
    public Ip2RegionUtils getSearcher() {
        log.info("开始创建ip2RegionUtils对象");
        return new Ip2RegionUtils();
    }

}
