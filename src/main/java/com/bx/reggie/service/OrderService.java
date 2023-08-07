package com.bx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.reggie.entity.Orders;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/7 15:32
 */
public interface OrderService extends IService<Orders> {
	public void submit(Orders orders);
}
