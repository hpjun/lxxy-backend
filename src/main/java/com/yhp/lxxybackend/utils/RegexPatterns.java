package com.yhp.lxxybackend.utils;

/**
 * @author yhp
 * @date 2024/3/29 14:51
 */

public abstract class RegexPatterns {
    /**
     * 手机号正则
     */
    public static final String PHONE_REGEX = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /**
     * 密码正则。长度在6~18之间，只能包含字母、数字和下划线
     */
    public static final String PASSWORD_REGEX = "^\\w{5,17}$";
    /**
     * 验证码正则, 6位数字
     */
    public static final String VERIFY_CODE_REGEX = "^\\d{6}$";
}
