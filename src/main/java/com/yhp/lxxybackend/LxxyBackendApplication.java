package com.yhp.lxxybackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yhp.lxxybackend.mapper")
public class LxxyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LxxyBackendApplication.class, args);
    }

}
