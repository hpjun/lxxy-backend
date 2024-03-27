package com.yhp.lxxybackend.constant;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:token:";

    // token有效期7天
    public static final long LOGIN_USER_TTL = 604800;
}
