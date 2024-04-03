package com.yhp.lxxybackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanToBeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yhp.lxxybackend.constant.BusinessConstant;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.model.dto.LoginUserDTO;
import com.yhp.lxxybackend.model.dto.Result;
import com.yhp.lxxybackend.model.dto.UserFormDTO;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.model.vo.UserCardVO;
import com.yhp.lxxybackend.service.UserService;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.utils.RegexUtils;
import com.yhp.lxxybackend.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        if(users.get(0).getBan() == 1){
            return Result.fail(MessageConstant.ACCOUNT_LOCKED);
        }

        if("admin".equals(role)){
            if(!(users.get(0).getRole().equals("admin"))){
                return Result.fail(MessageConstant.UN_AUTH);
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

    @Override
    public Result logout() {
        // 获取当前登录的token信息
        String token = UserHolder.getUser().getToken();
        // 在Redis中移除token信息
        stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token,0,TimeUnit.SECONDS);
        return Result.ok();
    }

    @Override
    public Result updatePwd(UserFormDTO userFormDTO) {
        String password = userFormDTO.getPassword();
        String code = userFormDTO.getCode();
        // 判断密码和验证码的合法性
        if(RegexUtils.isPasswordInvalid(password)){
            return Result.fail(MessageConstant.PASSWORD_FORMAT);
        }
        if(RegexUtils.isCodeInvalid(code)){
            return Result.fail(MessageConstant.CODE_FAIL);
        }
        // 获取当前登录的手机号
        Long userId = UserHolder.getUser().getId();
        User user = query().eq("id", userId).one();
        // 校验该手机号的验证码是否正确
        String realCode = stringRedisTemplate.opsForValue().get(RedisConstants.LOGIN_CODE_KEY + user.getPhone());
        if(!code.equals(realCode)){
            return Result.fail(MessageConstant.CODE_FAIL);
        }
        // 对用户的新密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        // 更新用户密码信息
        int update = userMapper.updateById(user);
        if(update != 0){
            stringRedisTemplate.expire(RedisConstants.LOGIN_CODE_KEY+user.getPhone(),0,TimeUnit.SECONDS);
            // 获取当前登录的token信息
            String token = UserHolder.getUser().getToken();
            // 在Redis中移除token信息
            stringRedisTemplate.expire(RedisConstants.LOGIN_USER_KEY+token,0,TimeUnit.SECONDS);
            return Result.ok("更改成功");
        }else{
            return Result.fail("服务器异常");
        }
    }

    @Override
    public Result<List<UserCardVO>> listUser(Integer pageNum, String sc, String ban) {
        // 根据条件分页查询用户信息

        // 封装查询条件
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Integer isBan;
        if("正常".equals(ban)){
            isBan = 0;
            userQueryWrapper.eq("ban",isBan);
        }else if("封禁".equals(ban)){
            isBan = 1;
            userQueryWrapper.eq("ban",isBan);
        }else{
            // 按照全部查询
        }
        if(!StrUtil.isBlank(sc)){
            userQueryWrapper
                    .and(qw -> qw
                            .like("username",sc)
                            .or().like("phone",sc)
                            .or().like("id",sc));
        }
        // 封装分页对象
        Page<User> page = new Page<>(pageNum,MessageConstant.ADMIN_PAGE_SIZE);
        // 分页查询
        Page<User> userPage = userMapper.selectPage(page, userQueryWrapper);
        List<User> users = userPage.getRecords();
        ArrayList<UserCardVO> userCardVOS = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (User user : users) {
            UserCardVO userCardVO = BeanUtil.copyProperties(user, UserCardVO.class);
            // 格式化日期
            userCardVO.setCreateTime(dateFormat.format(user.getCreateTime()));
            userCardVOS.add(userCardVO);
        }
        return Result.ok(userCardVOS,page.getTotal());
    }

    @Override
    public Result delete(List<Integer> ids) {
        // 判断该数组是否为空
        if(ids.size() == 0){
            return Result.fail("用户不存在，删除失败");
        }
        // TODO 删除之前，用户涉及的相关记录：活动、评论、收藏夹、帖子应该都被抹除


        userMapper.deleteBatchIds(ids);
        return Result.ok("删除成功");
    }

    @Override
    public Result changeStatus(Integer userId) {
        User user = userMapper.selectById(userId);
        if(user == null){
            return Result.fail(MessageConstant.USER_NOT_LOGIN);
        }
        Integer ban = user.getBan();
        if(ban == 0){
            // 被ban
            user.setBan(1);
            userMapper.updateById(user);
            return Result.ok(true);
        }else{
            // 解封
            user.setBan(0);
            userMapper.updateById(user);
            return Result.ok(false);
        }
    }
}




