package com.yhp.lxxybackend.service;

import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.dto.UserFormDTO;
import com.yhp.lxxybackend.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Admin
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-27 17:22:54
*/
public interface UserService extends IService<User> {

    Result register(UserFormDTO userFormDTO);

    Result login(UserFormDTO userFormDTO,String role);
}
