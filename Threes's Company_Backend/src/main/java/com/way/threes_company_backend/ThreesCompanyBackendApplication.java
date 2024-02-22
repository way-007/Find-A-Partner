package com.way.threes_company_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


//@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@SpringBootApplication
@MapperScan("com.way.threes_company_backend.mapper")
@EnableScheduling
public class ThreesCompanyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreesCompanyBackendApplication.class, args);
    }

}
