package com.bx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bx.reggie.entity.Category;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 15:17
 */
public interface CategoryService extends IService<Category> {
	void remove(Long id);
}
