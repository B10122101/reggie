package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.dto.SetmealDto;
import com.bx.reggie.entity.Setmeal;
import com.bx.reggie.entity.SetmealDish;
import com.bx.reggie.mapper.SetmealMapper;
import com.bx.reggie.service.SetmealDishService;
import com.bx.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:38
 */
@Service
@SuppressWarnings({"all"})
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
	@Autowired
	private SetmealDishService setmealDishService;
	
	@Override
	//新增套餐
	public void saveWithDish(SetmealDto setmealDto) {
		this.save(setmealDto);
		
		Long id = setmealDto.getId();
		
		List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
		setmealDishes = setmealDishes.stream().map((setmealDish) -> {
					setmealDish.setSetmealId(id);
					return setmealDish;
				}
		).collect(Collectors.toList());
		setmealDishService.saveBatch(setmealDishes);
	}
	
	//删除套餐
	@Override
	@Transactional
	public void removeWithDish(List<Long> ids) {
		LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.in(Setmeal::getId, ids);
		lambdaQueryWrapper.eq(Setmeal::getStatus, 1);
		long count = this.count(lambdaQueryWrapper);
		if (count > 0) {
			throw new RuntimeException("当前套餐证在售卖中，不能删除");
		}
		this.removeBatchByIds(ids);
		
		LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
		dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
		setmealDishService.remove(dishLambdaQueryWrapper);
		
	}
}
