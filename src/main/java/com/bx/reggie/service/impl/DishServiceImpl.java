package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.dto.DishDto;
import com.bx.reggie.entity.Dish;
import com.bx.reggie.entity.DishFlavor;
import com.bx.reggie.mapper.DishMapper;
import com.bx.reggie.service.DishFlavourService;
import com.bx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:38
 */
@Service
@Slf4j
@SuppressWarnings({"all"})
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
	@Autowired
	private DishFlavourService dishFlavourService;
	
	//添加菜品
	@Override
	@Transactional //由于此次添加菜品涉及到了多张表，所以需要开启事务
	public void saveWithFlavour(DishDto dishDto) {
		//将菜品的基本信息先添加到dish表中
		this.save(dishDto);
		//得到菜品id
		Long dishId = dishDto.getId();
		
		//往口味表中添加口味时要知道添加的口味是哪个菜品的，这里用菜品id区分
		//在封装时并没有封装菜品id到dishFlavour表中，所以需要在这里手动设置
		List<DishFlavor> flavors = dishDto.getFlavors();
		
		//Java8新特性将flavour集合中每个元素转换成流的形式，来操作每个元素(这里的每个元素是菜品的口味：甜度、温度、忌口辣度)
		flavors = flavors.stream().map((dishFlavor) -> {
					//取出每个口味，为每个口味都设置菜品的id
					dishFlavor.setDishId(dishId);
					//将设置好菜品id的口味元素返回
					return dishFlavor;
				}
				//collect是一个终止操作，它将流中的元素收集起来并转换成一个列表。
				//Collectors.toList()表示将流中的元素收集到一个新的List集合中。
		).collect(Collectors.toList());
		
		dishFlavourService.saveBatch(flavors);
	}
	
	//回显菜品信息
	@Override
	@Transactional
	public DishDto getByIdWithFlavour(Long id) {
		//回显dish
		Dish dish = this.getById(id);
		DishDto dishDto = new DishDto();
		//将查出来的dish中的信息拷贝给dishDto
		BeanUtils.copyProperties(dish, dishDto);
		
		//回显dish_flavour
		//构造条件器
		LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件
		dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish.getId());
		//执行查询回显
		List<DishFlavor> flavors = dishFlavourService.list(dishFlavorLambdaQueryWrapper);
		//将dish_flavour设置给dishDto
		dishDto.setFlavors(flavors);
		return dishDto;
	}
	
	
	//修改菜品
	@Transactional //由于此次修改菜品涉及到了多张表，所以需要开启事务
	public void updateWithFlavour(DishDto dishDto) {
		//修改dish表中信息
		this.updateById(dishDto);
		
		//修改dish_flavour表中信息
		//1、先将之前的口味全部删除
		LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
		dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
		dishFlavourService.remove(dishFlavorLambdaQueryWrapper);
		
		//2、再重新添加口味
		List<DishFlavor> flavors = dishDto.getFlavors();
		//Java8新特性将flavour集合中每个元素转换成流的形式，来操作每个元素(这里的每个元素是菜品的口味：甜度、温度、忌口辣度)
		flavors.stream().map((dishFlavor) -> {
					//取出每个口味，为每个口味都设置菜品的id
					dishFlavor.setDishId(dishDto.getId());
					//将设置好菜品id的口味元素返回
					return dishFlavor;
				}
				//collect是一个终止操作，它将流中的元素收集起来并转换成一个列表。
				//Collectors.toList()表示将流中的元素收集到一个新的List集合中。
		).collect(Collectors.toList());
		dishFlavourService.saveBatch(flavors);
	}
	
	
}
