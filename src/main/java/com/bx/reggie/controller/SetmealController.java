package com.bx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bx.reggie.dto.SetmealDto;
import com.bx.reggie.entity.Category;
import com.bx.reggie.entity.Setmeal;
import com.bx.reggie.service.CategoryService;
import com.bx.reggie.service.SetmealService;
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
@SuppressWarnings({"all"})
@RequestMapping("/setmeal")
public class SetmealController {
	@Autowired
	private SetmealService setmealService;
	@Autowired
	private CategoryService categoryService;
	
	//添加套餐
	@PostMapping
	public R<String> save(@RequestBody SetmealDto setmealDto) {
		setmealService.saveWithDish(setmealDto);
		return R.success("添加套餐成功");
	}
	
	
	//套餐分页查询
	@GetMapping("/page")
	public R<Page> page(int page, int pageSize, String name) {
		//构造分页过滤器
		Page<Setmeal> pageInfo = new Page<>(page, pageSize);
		//由于展示菜品时需要展示该菜品属于哪个菜系，而dish里只有菜系id，并没有菜系名字，
		// 所以返回结果应为dishDto(该类里有菜系名字字段)
		Page<SetmealDto> setmealDtoPage = new Page<>();
		//构造条件过滤器
		LambdaQueryWrapper<Setmeal> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件
		pageLambdaQueryWrapper.like(name != null, Setmeal::getName, name);
		//添加排序条件
		pageLambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);
		//执行查询
		setmealService.page(pageInfo, pageLambdaQueryWrapper);
		
		
		//对象拷贝，将dishPage中的信息拷贝到dishDtoPage里
		BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
		//但dishPage里的records记录不用拷贝，但是records里又有菜系id
		List<Setmeal> records = pageInfo.getRecords();
		
		List<SetmealDto> list = records.stream().map((res) -> {
					SetmealDto setmealDto = new SetmealDto();
					//将dishPage中的每条记录单独拷贝到dishDto里
					BeanUtils.copyProperties(res, setmealDto);
					//将dishPage中的每条记录里的菜系id取出来，查询id对应的菜系
					Long categoryId = res.getCategoryId();
					Category categoryName = categoryService.getById(categoryId);
					//给dishDto设置菜系名字
					if (categoryName != null) {
						setmealDto.setCategoryName(categoryName.getName());
					}
					return setmealDto;
				}
		).collect(Collectors.toList());//将记录封装为集合返回
		
		//设置dishDtoPage的记录
		setmealDtoPage.setRecords(list);
		return R.success(setmealDtoPage);
	}
	
	
	//删除套餐
	@DeleteMapping
	public R<String> delete(@RequestParam List<Long> ids) {
		setmealService.removeWithDish(ids);
		return R.success("删除成功");
	}
	
	
	
	@GetMapping("/list")
	public R<List<Setmeal>> list(Setmeal setmeal){
		//构造条件过滤器
		LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件
		setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
		setmealLambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,1);
		//添加排序
		setmealLambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);
		//执行回显
		List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);
		return R.success(list);
	}
}
