package com.yhp.lxxybackend;
import com.google.gson.Gson;
import com.yhp.lxxybackend.mapper.UserMapper;
import com.yhp.lxxybackend.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

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
        Gson gson = new Gson();
        User user = userMapper.selectById(1);
        String s = gson.toJson(user);
        stringRedisTemplate.opsForValue().set("test", s);
        String s1 = stringRedisTemplate.opsForValue().get("test");
        User user1 = gson.fromJson(s1, User.class);
        System.out.println("拿出的user"+user1);
    }
}
