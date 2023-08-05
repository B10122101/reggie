package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.ShoppingCart;
import com.bx.reggie.mapper.ShoppingCartMapper;
import com.bx.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 16:24
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
