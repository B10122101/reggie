package com.bx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/7 15:31
 */
@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
