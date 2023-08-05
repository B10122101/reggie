package com.bx.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 9:51
 */
@Configuration
//mybatis-plus分页插件
public class MybatisPlusConfig {
	//将其交给spring管理
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		//创建mybatis拦截器
		MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
		//分页拦截器添加到mybatis拦截器中
		mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
		return mybatisPlusInterceptor;
		
	}
}
