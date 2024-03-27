package com.yhp.lxxybackend.model.dto;

import lombok.Data;

@Data
public class LoginUserDTO {


    private Long id;
    private String username;
    private String avatar;
    private String token;
    private String role;

}
