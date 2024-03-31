package com.yhp.lxxybackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement //开启注解方式的事务管理
@EnableScheduling // 开启定时任务类
@MapperScan("com.yhp.lxxybackend.mapper")
public class LxxyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LxxyBackendApplication.class, args);
    }

}
