package com.yhp.lxxybackend.interceptor;

import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserLoginInterceptor implements HandlerInterceptor {

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
        return true;
    }
}
