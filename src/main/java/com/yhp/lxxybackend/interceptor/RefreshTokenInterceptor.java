package com.yhp.lxxybackend.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.utils.BusinessUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 刷新token的拦截器
 */

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 前置拦截器
//        Object user = request.getSession().getAttribute("user");




        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            // token为空，表示未登录
            return true;
        }
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash()
                .entries(RedisConstants.LOGIN_USER_KEY + token);
        if(userMap.isEmpty()){
            // 有token，但是Redis里没有，说明token是伪造的或过期了
            return true;
        }
        // Redis中的字段需要与LoginUserDTO的字段一致，username,id,avatar,role
        LoginUserDTO loginUserDTO = BeanUtil.fillBeanWithMap(userMap, new LoginUserDTO(), false);
        // 存在，保存到ThreadLocal
        UserHolder.saveUser(loginUserDTO);
        // 刷新token有效期
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY + token,RedisConstants.LOGIN_USER_TTL, TimeUnit.DAYS);
        // 放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 移除用户
        UserHolder.removeUser();
        // 进行PV统计
//        String uri = request.getRequestURI();
//        StringBuffer url = request.getRequestURL();
//        System.out.println("uri:"+uri+" url:"+url);
        String today = BusinessUtils.getToday();

        // 总pv
        stringRedisTemplate.opsForValue().increment(RedisConstants.TOTAL_PV_KEY);
        // 今日pv
        if(Boolean.FALSE.equals(stringRedisTemplate.hasKey(RedisConstants.DAY_PV_KEY + today))){
            stringRedisTemplate.opsForValue().increment(RedisConstants.DAY_PV_KEY+today);
            stringRedisTemplate.expire(RedisConstants.DAY_PV_KEY+today,RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);
        }else{
            stringRedisTemplate.opsForValue().increment(RedisConstants.DAY_PV_KEY+today);
        }
    }
}
