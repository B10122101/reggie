package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.Employee;
import com.bx.reggie.mapper.EmployeeMapper;
import com.bx.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/1 9:59
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
