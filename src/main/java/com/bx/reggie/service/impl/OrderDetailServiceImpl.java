package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.OrderDetail;
import com.bx.reggie.mapper.OrderDetailMapper;
import com.bx.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/7 15:37
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>implements OrderDetailService {
}
