package com.yhp.lxxybackend.constant;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:token:";

    // token有效期7天
    public static final long LOGIN_USER_TTL = 7L;
    public static final String LOGIN_CODE_KEY = "phone:code:";
    public static final String LOGIN_TOKEN = "login:token:";
    public static final long LOGIN_CODE_TTL = 2L;
    public static final long LOGIN_ADMIN_TTL = 1L;
}
