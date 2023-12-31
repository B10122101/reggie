package com.bx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement  //添加菜品那设计到多张表需开启事务
@EnableCaching //开启缓存
public class ReggieApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ReggieApplication.class, args);
	}
	
}
