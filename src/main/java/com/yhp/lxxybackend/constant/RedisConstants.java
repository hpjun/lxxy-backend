package com.yhp.lxxybackend.constant;

public class RedisConstants {
    public static final String LOGIN_USER_KEY = "login:token:";

    // token有效期7天
    public static final long LOGIN_USER_TTL = 7L;
    public static final String LOGIN_CODE_KEY = "phone:code:";
    public static final long LOGIN_CODE_TTL = 2L;
    public static final long LOGIN_ADMIN_TTL = 1L;
    // 日统计的有效期为32天
    public static final long STATISTICS_DAY_TTL = 32L;
    // 用户推荐列表有效期1
    public static final long RANDOM_TTL = 1L;

    public static final String USER_INBOX = "user:inbox:";


    public static final String POST_VIEW_COUNT = "statistics:post:viewCount:";

    public static final String TOTAL_UV_KEY = "statistics:total:uv";
    public static final String TOTAL_PV_KEY = "statistics:total:pv";
    public static final String USER_REGION_KEY = "statistics:userRegion";
    public static final String ACTIVITY_JOIN_RATE = "statistics:activityJoinRate";


    public static final String DAY_UV_KEY = "statistics:day:uv:";
    public static final String DAY_PV_KEY = "statistics:day:pv:";
    public static final String DAY_POST_KEY = "statistics:day:postCount:";
    public static final String DAY_ACTIVITY_KEY = "statistics:day:activityCount:";
    public static final String DAY_USER_KEY = "statistics:day:userCount:";


    public static final String HOUR_PV_KEY = "statistics:24h:pv";
    public static final String HOUR_POST_KEY = "statistics:24h:postCount";
    public static final String HOUR_USER_KEY = "statistics:24h:userCount";
    public static final String HOUR_ACTIVITY_KEY = "statistics:24h:activityCount";
    public static final String HOUR_UV_KEY = "statistics:24h:uv:";


    public static final String HOT_POST_KEY = "hot:post";
    public static final String DEFAULT_RANDOM = "random:default";
    public static final String USER_RANDOM = "random:user:";
    public static final Double VIEW_SCORE = 0.1;
    public static final Double COMMENT_SCORE = 20.0;
    public static final Double FAVORITE_SCORE = 30.0;
}
