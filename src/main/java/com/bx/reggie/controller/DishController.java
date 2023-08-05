package com.bx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bx.reggie.dto.DishDto;
import com.bx.reggie.entity.Category;
import com.bx.reggie.entity.Dish;
import com.bx.reggie.entity.DishFlavor;
import com.bx.reggie.service.CategoryService;
import com.bx.reggie.service.DishFlavourService;
import com.bx.reggie.service.DishService;
import com.bx.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:43
 */
@RestController
@Slf4j
@RequestMapping("/dish")

public class DishController {
	@Autowired
	private DishService dishService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private DishFlavourService dishFlavourService;
//	@Autowired
	
	
	//添加菜品
	@PostMapping
	public R<String> save(@RequestBody DishDto dishDto) {
		dishService.saveWithFlavour(dishDto);
		return R.success("添加菜品成功");
	}
	
	//菜品分页查询
	@GetMapping("/page")
	public R<Page> page(int page, int pageSize, String name) {
		//构造分页过滤器
		Page<Dish> pageInfo = new Page<>(page, pageSize);
		//由于展示菜品时需要展示该菜品属于哪个菜系，而dish里只有菜系id，并没有菜系名字，
		// 所以返回结果应为dishDto(该类里有菜系名字字段)
		Page<DishDto> dishDtoPage = new Page<>();
		
		//构造条件过滤器
		LambdaQueryWrapper<Dish> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件
		pageLambdaQueryWrapper.like(name != null, Dish::getName, name);
		//添加排序条件
		pageLambdaQueryWrapper.orderByAsc(Dish::getUpdateTime);
		//执行查询
		dishService.page(pageInfo, pageLambdaQueryWrapper);
		
		//对象拷贝，将dishPage中的信息拷贝到dishDtoPage里
		BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
		//但dishPage里的records记录不用拷贝，但是records里又有菜系id
		List<Dish> records = pageInfo.getRecords();
		
		List<DishDto> list = records.stream().map((res) -> {
					DishDto dishDto = new DishDto();
					//将dishPage中的每条记录单独拷贝到dishDto里
					BeanUtils.copyProperties(res, dishDto);
					//将dishPage中的每条记录里的菜系id取出来，查询id对应的菜系
					Long categoryId = res.getCategoryId();
					Category categoryName = categoryService.getById(categoryId);
					//给dishDto设置菜系名字
					if (categoryName != null) {
						dishDto.setCategoryName(categoryName.getName());
					}
					return dishDto;
				}
		).collect(Collectors.toList());//将记录封装为集合返回
		
		//设置dishDtoPage的记录
		dishDtoPage.setRecords(list);
		return R.success(dishDtoPage);
	}
	
	//回显菜品
	@GetMapping("/{id}")
	public R<DishDto> select(@PathVariable Long id) {
		DishDto dishWithFlavour = dishService.getByIdWithFlavour(id);
		return R.success(dishWithFlavour);
	}
	
	//修改菜品
	@PutMapping
	public R<String> update(@RequestBody DishDto dishDto) {
		dishService.updateWithFlavour(dishDto);
		return R.success("修改成功");
	}
	
	
	//菜品展示到用户端点菜
	@GetMapping("/list")
	public R<List<DishDto>> list(Dish dish) {
		//构造条件过滤器
		LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件，菜品id
		dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
		//菜品在售卖
		dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
		//添加排序
		dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
		//执行回显每一个菜品
		List<Dish> list = dishService.list(dishLambdaQueryWrapper);
		//用户端点菜时需展示菜品口味
		List<DishDto> dishDtoList = list.stream().map((res) -> {
					DishDto dishDto = new DishDto();
					//将菜品中回显的每个菜品单独拷贝到dishDto里
					BeanUtils.copyProperties(res, dishDto);
					//用户端是根据菜系展示的
					//将的每个菜品的菜系id取出来，查询id对应的菜系名字
					Long categoryId = res.getCategoryId();
					Category category = categoryService.getById(categoryId);
					
					//如果该菜品对应的菜系不为空
					//给dishDto设置菜系名字
					if (category != null) {
						dishDto.setCategoryName(category.getName());
					}
					
					//得到每个菜品id
					Long dishId = res.getId();
					LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
					dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
					//查询每个菜品的口味
					List<DishFlavor> dishFlavorList = dishFlavourService.list(dishFlavorLambdaQueryWrapper);
					//将复制到dishDto里的菜添加口味进去
					dishDto.setFlavors(dishFlavorList);
					return dishDto;
				}
		).collect(Collectors.toList());//将dishDto里的添加上菜系和口味的每个菜品封装成集合返回
		
		//将添加了菜系和口味的菜品集合返回给前端
		return R.success(dishDtoList);
	}
}
