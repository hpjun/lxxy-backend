package com.yhp.lxxybackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author Admin
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-27 17:22:54
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




