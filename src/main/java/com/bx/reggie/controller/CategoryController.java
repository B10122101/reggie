package com.bx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bx.reggie.entity.Category;
import com.bx.reggie.entity.Employee;
import com.bx.reggie.service.CategoryService;
import com.bx.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:20
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
	@Autowired
	public CategoryService categoryService;
	
	//添加菜品或套餐
	@PostMapping
	public R<String> save(@RequestBody Category category) {
		categoryService.save(category);
		return R.success("添加成功");
	}
	
	//菜品或套餐分类分页查询
	@GetMapping("/page")
	public R<Page> page(int page, int pageSize) {
		//构造分页过滤器
		Page<Category> pageInfo = new Page(page, pageSize);
		//构造条件过滤器
		LambdaQueryWrapper<Category> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加排序条件
		pageLambdaQueryWrapper.orderByAsc(Category::getSort);
		//执行查询
		categoryService.page(pageInfo, pageLambdaQueryWrapper);
		return R.success(pageInfo);
	}
	
	
	//菜品或套餐删除
	@DeleteMapping()
	public R<String> delete( Long ids){
		log.info("=====================================id为：{}",ids);
		categoryService.remove(ids);
		return R.success("删除成功");
	}
	
	//菜品或套餐修改
	@PutMapping
	public R<String> update(@RequestBody Category category){
		categoryService.updateById(category);
		return R.success("修改成功");
	}
	
	//添加菜品时回显选择菜品类别
	@GetMapping("/list")
	public R<List<Category>> list(Category category){
		//构造条件过滤器
		LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件，根据菜品type
		categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
		//添加排序
		categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
		//执行回显
		List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
		return R.success(list);
	}

	
	
}
