package com.yhp.lxxybackend.model.dto;

import lombok.Data;

/**
 * @author yhp
 * @date 2024/3/27 22:11
 */

@Data
public class UserFormDTO {
    private String phone;			//手机号
    private String code;				//验证码
    private String password;		//密码
}
