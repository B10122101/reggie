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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:43
 */
@RestController
@Slf4j
@RequestMapping("/dish")
@SuppressWarnings({"all"})
public class DishController {
	@Autowired
	private DishService dishService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private DishFlavourService dishFlavourService;
    @Autowired
	private RedisTemplate redisTemplate;
	
	
	//添加菜品
	@PostMapping
	public R<String> save(@RequestBody DishDto dishDto) {
		String key="Dish_"+dishDto.getCategoryId()+"_1";
		dishService.saveWithFlavour(dishDto);
		redisTemplate.delete(key);
		
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
		pageLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
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
		String key="Dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
		dishService.updateWithFlavour(dishDto);
		redisTemplate.delete(key);
		return R.success("修改成功");
	}
	

	//删除菜品
	@DeleteMapping()
	public R<String> delete( Long ids){
		Dish dish = dishService.getById(ids);
		String key="Dish_"+dish.getCategoryId()+"_"+dish.getStatus();
		dishService.removeById(ids);
		redisTemplate.delete(key);
		return R.success("删除菜品成功");
	}
	
	
	
	//菜品展示到用户端点菜
	@GetMapping("/list")
	public R<List<DishDto>> list(Dish dish) {
		List<DishDto> dishDtoList=null;
		//构造key，根据菜系id和在售状态
		String key = "Dish_"+dish.getCategoryId()+"_"+dish.getStatus();
		log.info("========key为====================================================={}",key);
		//返回该key在redis缓存中的菜品
		 dishDtoList =(List<DishDto>) redisTemplate.opsForValue().get(key);
		 
		 
		//如果不为空，直接返回给前端
		 if(dishDtoList!=null){
			 log.info("-----------缓存中dishDtoList为-------------------------------------{}",dishDtoList);
			 return  R.success(dishDtoList);
		 }
		 //为空则再去数据库中查
		
		//构造条件过滤器
		LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件，菜品id
		dishLambdaQueryWrapper.eq(Dish::getCategoryId, dish.getCategoryId());
		//菜品在售卖
		dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
		//添加排序
		dishLambdaQueryWrapper.orderByDesc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
		//执行回显每一个菜品
		List<Dish> list = dishService.list(dishLambdaQueryWrapper);
		//用户端点菜时需展示菜品口味
		dishDtoList = list.stream().map((res) -> {
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
		
		log.info("-----------数据库中dishDtoList为-----------------------------------------{}",dishDtoList);
		//将查到的菜品添加到redis缓存中
		redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);
		
		//将添加了菜系和口味的菜品集合返回给前端
		return R.success(dishDtoList);
	}
}
