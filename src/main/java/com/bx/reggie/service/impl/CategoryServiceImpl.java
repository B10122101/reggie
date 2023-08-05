package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.Category;
import com.bx.reggie.entity.Dish;
import com.bx.reggie.entity.Setmeal;
import com.bx.reggie.mapper.CategoryMapper;
import com.bx.reggie.service.CategoryService;
import com.bx.reggie.service.DishService;
import com.bx.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:19
 */
@Service
@Slf4j
@SuppressWarnings({"all"})
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
	@Autowired
	private CategoryMapper categoryMapper;
	@Autowired
	private DishService dishService;
	@Autowired
	private SetmealService setmealService;
	
	//菜品或套餐删除
	@Transactional
	public void remove(Long id){
		
		//要删除菜品或套菜分类前要判断当前要删除的菜品或套餐分类下有没有绑定菜品或套餐
		//构造菜品条件查询器
		LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper();
		//添加查询条件，根据菜品id
		dishQueryWrapper.eq(Dish::getCategoryId,id);
		log.info("=================================================id为：{}",id);
		//执行查询菜品数量
		long dish = dishService.count(dishQueryWrapper);
		//如果查询到当前要删除的菜品或套餐分类下有菜品，则不能删除
		if (dish>0){
			throw new RuntimeException("当前分类下有菜品，不能删除");
		}
		//构造套餐条件查询器
		LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper();
		//添加查询条件，根据套餐id
		setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
		//执行查询套餐
		long setmeal = setmealService.count(setmealQueryWrapper);
		//如果查询到当前要删除的菜品或套餐分类下有套餐，则不能删除
		if (setmeal>0){
			throw new RuntimeException("当前分类下有套餐，不能删除");
		}
		//没有绑定菜品或套餐就成功删除
		super.removeById(id);
	}
}
