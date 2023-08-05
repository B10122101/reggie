package com.bx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.reggie.dto.DishDto;
import com.bx.reggie.entity.Dish;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:37
 */
public interface DishService extends IService<Dish> {
	void saveWithFlavour(DishDto dishDto);
	
	DishDto getByIdWithFlavour(Long id);
	void updateWithFlavour(DishDto dishDto);
}
