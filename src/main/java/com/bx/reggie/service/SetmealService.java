package com.bx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.reggie.dto.SetmealDto;
import com.bx.reggie.entity.Dish;
import com.bx.reggie.entity.Setmeal;

import java.util.List;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:37
 */
public interface SetmealService extends IService<Setmeal> {
	void saveWithDish(SetmealDto setmealDto);
	
	void removeWithDish(List<Long> ids);
}
