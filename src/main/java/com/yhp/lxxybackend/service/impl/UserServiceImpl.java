package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanToBeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.BusinessConstant;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.dto.UserFormDTO;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Admin
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-03-27 17:22:54
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    UserMapper userMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public Result register(UserFormDTO userFormDTO) {
        String phone = userFormDTO.getPhone();
        String password = userFormDTO.getPassword();
        String code = userFormDTO.getCode();
        // 校验手机号、密码、验证码
        if (RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail(MessageConstant.PHONE_FORMAT);
        }
        String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
        if (RegexUtils.isCodeInvalid(code) || !code.equals(realCode)) {
            return Result.fail(MessageConstant.CODE_FAIL);
        }
        if (RegexUtils.isPasswordInvalid(password)) {
            return Result.fail(MessageConstant.PASSWORD_FORMAT);
        }
        // 查询手机号，如果数据库存在手机号说明该手机号已注册
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        List<User> users = userMapper.selectByMap(map);
        if (users.size() != 0) {
            return Result.fail(MessageConstant.PHONE_REGISTERED);
        }

        User user = new User();
        // 对密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        // 封装User对象
        user.setPhone(phone);
        user.setUsername(phone);
        user.setAvatar(BusinessConstant.DEFAULT_USER_AVATAR);
        user.setProfile(BusinessConstant.DEFAULT_USER_PROFILE);
        user.setSex(BusinessConstant.DEFAULT_USER_SEX);
        user.setAddress(BusinessConstant.DEFAULT_USER_ADDRESS);
        // 存入数据库
        int success = userMapper.insert(user);
        if (success == 0) {
            return Result.fail(MessageConstant.SERVER_ERROR);
        } else {
            // 将验证码置为失效
            stringRedisTemplate.expire(RedisConstants.LOGIN_CODE_KEY + phone, 0, TimeUnit.SECONDS);
            return Result.ok();
        }
    }

    @Override
    public Result login(UserFormDTO userFormDTO,String role) {
        String phone = userFormDTO.getPhone();
        String password = userFormDTO.getPassword();
        String code = userFormDTO.getCode();

        Map<String, Object> map = new HashMap<>();
        List<User> users = null;
        map.put("phone", phone);

        if(StrUtil.isBlank(password) && StrUtil.isBlank(code)){
            return Result.fail("请输入密码或验证码");
        }

        if (!StrUtil.isBlank(password)) {
            // 进行手机好格式验证
            if (RegexUtils.isPhoneInvalid(phone)) {
                return Result.fail(MessageConstant.PHONE_PASSWORD_ERROR);
            }
            // 进行密码格式比对
            if (RegexUtils.isPasswordInvalid(password)) {
                return Result.fail(MessageConstant.PHONE_PASSWORD_ERROR);
            }
            // 用户查询
            users = userMapper.selectByMap(map);
            if (users.size() == 0) {
                return Result.fail(MessageConstant.UN_REGISTER);
            }
            // 密码比对
            password = DigestUtils.md5DigestAsHex(password.getBytes());
            if (!users.get(0).getPassword().equals(password)) {
                return Result.fail(MessageConstant.PHONE_PASSWORD_ERROR);
            }
        }
        if (!StrUtil.isBlank(code)) {
            // 进行手机好格式验证
            if (RegexUtils.isPhoneInvalid(phone)) {
                return Result.fail(MessageConstant.PHONE_FORMAT);
            }
            // 进行验证码格式比对
            if (RegexUtils.isCodeInvalid(code)) {
                return Result.fail(MessageConstant.CODE_FAIL);
            }
            // 用户查询
            users = userMapper.selectByMap(map);
            if (users.size() == 0) {
                return Result.fail(MessageConstant.UN_REGISTER);
            }
            // 验证码比对
            String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + phone);
            if (!code.equals(realCode)) {
                return Result.fail(MessageConstant.CODE_FAIL);
            }
        }
        if("admin".equals(role)){
            if(!(users.get(0).getRole().equals("admin"))){
                return Result.fail("无权限");
            }
        }


        // 生成token作为令牌
        String token = IdUtil.getSnowflake().nextIdStr();
        Long id = users.get(0).getId();
        token = token + id;
        assert users != null;
        LoginUserDTO loginUserDTO = BeanUtil.copyProperties(users.get(0), LoginUserDTO.class);
        loginUserDTO.setToken(token);
        // beanTOMap!!
        Map<String, Object> userMap = BeanUtil.beanToMap(loginUserDTO,new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));

        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        // 将token放入Redis
        if("admin".equals(role)) {
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            stringRedisTemplate.expire(tokenKey, RedisConstants.LOGIN_ADMIN_TTL, TimeUnit.DAYS);

        }else{
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            stringRedisTemplate.expire(tokenKey, RedisConstants.LOGIN_USER_TTL, TimeUnit.DAYS);

        }
        return Result.ok(token);
    }
}




