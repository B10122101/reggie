package com.bx.reggie.controller;

import com.bx.reggie.entity.Orders;
import com.bx.reggie.service.OrderService;
import com.bx.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/7 15:35
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {
	@Autowired
	private OrderService orderService;
	
	@PostMapping("/submit")
	public R<String> submit(@RequestBody Orders orders){
		orderService.submit(orders);
		return  R.success("下单成功");
	}
	
}
