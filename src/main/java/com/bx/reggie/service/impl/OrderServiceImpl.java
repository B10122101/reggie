package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.*;
import com.bx.reggie.mapper.OrderMapper;
import com.bx.reggie.service.*;
import com.bx.reggie.utils.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/7 15:34
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressBookService addressBookService;
	
	@Autowired
	private OrderDetailService orderDetailService;
	
	@Override
	@Transactional
	//用户下单
	public void submit(Orders orders) {
		//获取用户id
		Long currentId = BaseContext.getCurrentId();
		
		//查看用户购物车数据
		LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
		shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
		List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
		if (shoppingCarts == null && shoppingCarts.size() == 0) {
			throw new RuntimeException("购物车为空，不能下单");
		}
		
		//查询用户地址
		User user = userService.getById(currentId);
		Long addressBookId = orders.getAddressBookId();
		AddressBook addressBook = addressBookService.getById(addressBookId);
		if (addressBook == null) {
			throw new RuntimeException("未设置地址，不能下单");
		}
		
		//向订单表插入数据，一条数据
		//订单号
		long orderId = IdWorker.getId();
		
		AtomicInteger amount = new AtomicInteger(0);
		
		//订单明细数据
		List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrderId(orderId);
			orderDetail.setNumber(item.getNumber());
			orderDetail.setDishFlavor(item.getDishFlavor());
			orderDetail.setDishId(item.getDishId());
			orderDetail.setSetmealId(item.getSetmealId());
			orderDetail.setName(item.getName());
			orderDetail.setImage(item.getImage());
			orderDetail.setAmount(item.getAmount());
			amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
			return orderDetail;
		}).collect(Collectors.toList());
		
		//设置订单数据
		orders.setId(orderId);
		orders.setOrderTime(LocalDateTime.now());
		orders.setCheckoutTime(LocalDateTime.now());
		orders.setStatus(2);
		orders.setAmount(new BigDecimal(amount.get()));//总金额
		orders.setUserId(currentId);
		orders.setNumber(String.valueOf(orderId));
		orders.setUserName(user.getName());
		orders.setConsignee(addressBook.getConsignee());
		orders.setPhone(addressBook.getPhone());
		orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
				+ (addressBook.getCityName() == null ? "" : addressBook.getCityName())
				+ (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
				+ (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
		this.save(orders);
		
		//向订单明细表插入数据，多条数据
		orderDetailService.saveBatch(orderDetails);
		
		//清空购物车数据
		shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
	}
}
