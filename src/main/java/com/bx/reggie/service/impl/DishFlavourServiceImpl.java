package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.DishFlavor;
import com.bx.reggie.mapper.DishFlavourMapper;
import com.bx.reggie.service.DishFlavourService;
import org.springframework.stereotype.Service;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/3 8:30
 */
@Service
public class DishFlavourServiceImpl extends ServiceImpl<DishFlavourMapper,DishFlavor>implements DishFlavourService {
}
