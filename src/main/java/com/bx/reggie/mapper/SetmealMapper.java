package com.bx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.reggie.entity.Dish;
import com.bx.reggie.entity.Setmeal;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:36
 */
@Mapper
	public interface SetmealMapper extends BaseMapper<Setmeal> {
}
