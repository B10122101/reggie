package com.bx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bx.reggie.entity.User;
import com.bx.reggie.service.UserService;
import com.bx.reggie.utils.R;
import com.bx.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 8:50
 */
@RestController
@Slf4j
@SuppressWarnings({"all"})
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	//使用redis缓存优化验证码
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	//发送验证码
	@PostMapping("/sendMsg")
	public R<String> sendMsg(@RequestBody User user, HttpSession session) {
		//获取手机号
		String phone = user.getPhone();
		
		//前端传过来的手机号不为空就给该手机号发送验证码
		if (!StringUtils.isEmpty(phone)) {
			//给手机号发送验证码短信
			String code = ValidateCodeUtils.generateValidateCode(6).toString();

//			//发送验证码短信成功就将手机号和验证码保存到session中，后面用户输入验证码登录时需要验证
//			session.setAttribute(phone, code);
			
			//使用redis来保存手机号和验证码并设置有效时间为5分钟
			redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
			
			log.info("验证码为：{}", code);
			return R.success("发送验证码成功");
		}
		return R.success("发送验证码失败");
		
	}
	
	
	//用户登录
	@PostMapping("/login")
	public R<User> login(@RequestBody Map map, HttpSession session) {
//		//获取手机号
//		String phone =map.get("phone").toString();
//		//获取验证码
//		String code = map.get("code").toString();
//		//获取session中保存的验证码
//		String codeInSeesion = session.getAttribute(code).toString();
		
		//从redis中取得保存的验证码
//		String redisCode =(String) redisTemplate.opsForValue().get(phone);
//		//比对验证码
//		if(redisCode!=null&&code.equals(redisCode)){
		//判断是否注册过
		LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
		userLambdaQueryWrapper.eq(User::getPhone, map.get("phone"));
		User user = userService.getOne(userLambdaQueryWrapper);
		//没有注册过就注册
		if (user == null) {
			User user1 = new User();
			//设置手机号
			user1.setPhone(map.get("phone").toString());
			//新增注册
			userService.save(user1);
		}
		//登录成功，保存用户id
		session.setAttribute("user", user.getId());
		//删除此次验证码
//		redisTemplate.delete(phone);
		return R.success(user);

//		}
//
//		return R.error("登录失败");
		
	}
}
