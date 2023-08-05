package com.bx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bx.reggie.entity.ShoppingCart;
import com.bx.reggie.service.ShoppingCartService;
import com.bx.reggie.utils.BaseContext;
import com.bx.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 16:25
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
@SuppressWarnings({"all"})
public class ShoppingCartController {
	
	@Autowired
	private ShoppingCartService shoppingCartService;
	
	//添加菜品或套餐到购物车
	@PostMapping("/add")
	public R<String> add(@RequestBody ShoppingCart shoppingCart) {
		log.info("要添加到购物车的数据：{}",shoppingCart.toString());
		//是哪个用户在添加
		Long userId = BaseContext.getCurrentId();
		//给当前购物车设置用户id
		shoppingCart.setUserId(userId);
		
		//添加的是菜品还是套餐(根据用户id和菜品id，根据用户id和套餐id)
		LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//设置用户id条件
		shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
		
		//得到要添加到购物车的菜品id和套餐id
		Long dishId = shoppingCart.getDishId();
		Long setmealId = shoppingCart.getSetmealId();
		
		//如果菜品id不为空说明当前添加到购物车的是菜品
		if(dishId!=null){
			//设置菜品id
			shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
			//判断当前用户的购物车中前有没有添加过该菜品
			ShoppingCart dish= shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
			//之前没有添加过就将该菜品添加到购物车
			if(dish==null){
				shoppingCart.setCreateTime(LocalDateTime.now());
				shoppingCartService.save(shoppingCart);
				//之间添加过就将该菜品数量+1
			}else {
				Integer number = dish.getNumber();
				dish.setNumber(number+1);
				shoppingCartService.updateById(dish);
			}
		//否则说明当前添加到购物车的是套餐
		}else{
			//设置套餐id
			shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
			//判断当前用户的购物车中前有没有添加过该套餐
			ShoppingCart setmeal = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
			//之前没有添加过就更新将该套餐添加到购物车
			if(setmeal==null){
				shoppingCart.setCreateTime(LocalDateTime.now());
				shoppingCartService.save(shoppingCart);
				//之前添加过就更新将该套餐数量+1
			}else {
				Integer number = setmeal.getNumber();
				setmeal.setNumber(number+1);
				shoppingCartService.updateById(setmeal);
			}
		}
		return R.success("添加到购物车成功");
	}
	
	//购物车展示
	@GetMapping("/list")
	public R<List<ShoppingCart>> list() {
		//创建条件器
		LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件，根据用户id展示
		shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
		//根据时间排序
		shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
		//查询展示
		List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
		return R.success(list);
	}
	
	//清空用户购物车，根据用户id来清空
	@DeleteMapping("/clean")
	public R<String> delete(){
		//得到当前登录用户id
		Long userId = BaseContext.getCurrentId();
		//构造条件器
		LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件，根据用户id
		shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
		//执行清空
		shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
		return R.success("清空购物车成功");
		
	}

}
