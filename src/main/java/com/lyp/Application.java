package com.lyp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

/**
 * 启动程序
 *
 * @author ruoyi
 */
@SpringBootApplication
@MapperScan("com.lyp.test.mapper.**")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);


    }

    @PostConstruct
    public void init() throws Exception {
    }
}