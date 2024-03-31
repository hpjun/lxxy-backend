package com.yhp.lxxybackend.interceptor;

import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.utils.BusinessUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class UserLoginInterceptor implements HandlerInterceptor {


    private StringRedisTemplate stringRedisTemplate;

    public UserLoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 判断是否需要拦截（ThreadLocal中有没有用户信息）
        LoginUserDTO user = UserHolder.getUser();
        if(user == null){
            // 没有，拦截
            response.setStatus(401);
            return false;
        }
        // 有用户，放行

        Long userId = user.getId();
        // 进行UV统计
        // 24hUV，定时任务会进行删除操作，这里只管写就是了
        String hour = BusinessUtils.getHour();
        stringRedisTemplate.opsForHyperLogLog().add(RedisConstants.HOUR_UV_KEY+hour,String.valueOf(userId));
        // 每天UV,有效期为32天
        String today = BusinessUtils.getToday();
        if(Boolean.FALSE.equals(stringRedisTemplate.hasKey(RedisConstants.DAY_UV_KEY + today))){
            stringRedisTemplate.opsForHyperLogLog().add(RedisConstants.DAY_UV_KEY+today,String.valueOf(userId));
            stringRedisTemplate.expire(RedisConstants.DAY_UV_KEY+today,RedisConstants.STATISTICS_DAY_TTL,TimeUnit.DAYS);
        }else{
            stringRedisTemplate.opsForHyperLogLog().add(RedisConstants.DAY_UV_KEY+today,String.valueOf(userId));
        }
        // 总UV
        stringRedisTemplate.opsForHyperLogLog().add(RedisConstants.TOTAL_UV_KEY,String.valueOf(userId));
        // 放行
        return true;
    }
}
