package com.yhp.lxxybackend;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yhp.lxxybackend.constant.BusinessConstant;
import com.yhp.lxxybackend.constant.MessageConstant;
import com.yhp.lxxybackend.constant.RedisConstants;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.model.entity.User;
import com.yhp.lxxybackend.model.vo.UserCardVO;
import com.yhp.lxxybackend.utils.BusinessUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.DigestUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@SpringBootTest
class LxxyBackendApplicationTests {

   @Autowired
   UserMapper userMapper;

   @Autowired
   StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
        System.out.println(userMapper.selectById(1));
    }

    @Test
    void redisTest() {
//        Gson gson = new Gson();
//        User user = userMapper.selectById(1);
//        String s = gson.toJson(user);
//        stringRedisTemplate.opsForValue().set("test", s);
//        String s1 = stringRedisTemplate.opsForValue().get("test");
//        User user1 = gson.fromJson(s1, User.class);
//        System.out.println("拿出的user"+user1);

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime of = LocalDateTime.of(2024, 3, 30, 17, 30);
        userQueryWrapper.lt("create_time",of);
        List<User> users = userMapper.selectList(userQueryWrapper);
        users.forEach(System.out::println);
    }

    @Test
    void UUIDTest(){
        Snowflake snowflake = IdUtil.getSnowflake();
        for (int i = 0; i < 1000; i++) {
//            System.out.println(UUID.randomUUID().toString(true));
            System.out.println(snowflake.nextId());
        }
//        System.out.println(UUID.randomUUID().toString(true));
//        System.out.println(IdUtil.getSnowflakeNextId());
//        System.out.println(UUID.fastUUID().toString(true).length());
//        System.out.println(UUID.randomUUID(false));
//        System.out.println(UUID.randomUUID(true));
    }

    @Test
    void beanTest(){

        String sc = "";
        String ban = "全部";
        Integer pageNum = 1;


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
        Page<User> page = new Page<>(pageNum, MessageConstant.ADMIN_PAGE_SIZE);
        // 分页查询
        Page<User> userPage = userMapper.selectPage(page, userQueryWrapper);
        List<User> users = userPage.getRecords();
        ArrayList<UserCardVO> userCardVOS = new ArrayList<>();
        for (User user : users) {
            UserCardVO userCardVO = BeanUtil.copyProperties(user, UserCardVO.class);
            userCardVOS.add(userCardVO);
        }
    }



    /**
     * 批量插入假用户数据
     */
    @Test
    void addUserTest(){
        User user = new User();
        // 对密码进行MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex("123123".getBytes()));
        // 封装User对象
        user.setAvatar(BusinessConstant.DEFAULT_USER_AVATAR);
        user.setProfile(BusinessConstant.DEFAULT_USER_PROFILE);
        user.setSex(BusinessConstant.DEFAULT_USER_SEX);
        user.setAddress(BusinessConstant.DEFAULT_USER_ADDRESS);

        String phoneStr = "15112461";
        String phone;
        String format = "%03d"; // 格式化为四位数字，不足四位补零

        for (int i = 0; i < 999; i++) {
            String format1 = String.format(format, i);
            phone = phoneStr + format1;
            user.setId(null);
            user.setPhone(phone);
            user.setUsername(phone);
            userMapper.insert(user);
        }
    }
}
