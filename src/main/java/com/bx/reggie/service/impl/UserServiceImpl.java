package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.User;
import com.bx.reggie.mapper.UserMapper;
import com.bx.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 8:49
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {
}
