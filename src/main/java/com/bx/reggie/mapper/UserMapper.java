package com.bx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 8:48
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
