package com.bx.reggie.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/1 15:28
 */
@RestControllerAdvice
@ResponseBody
@Slf4j
//全局异常处理器
/**
 * 1、@RestControllerAdvice 全局异常处理器注解
 * 2、@ExceptionHandler(Exception.class) 括号里填要捕获的异常类型
 */
public class GlobalExceptionHandler {
	@ExceptionHandler(Exception.class)
	public R<String> exceptionHandler(Exception ex) {
		log.error(ex.getMessage());
		return R.error(ex.getMessage());
	}
}
