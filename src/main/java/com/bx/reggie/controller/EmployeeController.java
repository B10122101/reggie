package com.bx.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bx.reggie.utils.R;
import com.bx.reggie.entity.Employee;
import com.bx.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/1 10:01
 */
@Slf4j
@SuppressWarnings({"all"})
@RestController
@RequestMapping("/employee")
public class EmployeeController {
	@Autowired
	private EmployeeService employeeService;
	
	//登录
	@PostMapping("/login")
	public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
		//密码加密
		String password = employee.getPassword();
		password = DigestUtils.md5DigestAsHex(password.getBytes());
		//构建条件过滤器
		LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
		//添加条件
		queryWrapper.eq(Employee::getUsername, employee.getUsername());
		//查询
		Employee emp = employeeService.getOne(queryWrapper);
		//查询失败
		if (emp == null) {
			R.error("登录失败");
		}
		//密码错误
		if (!emp.getPassword().equals(password)) {
			return R.error("登录失败");
		}
		//用户已被禁用
		if (emp.getStatus() == 0) {
			return R.error("账户已禁用");
		}
		//登录成功
		request.getSession().setAttribute("employee", emp.getId());
		return R.success(emp);
	}
	
	//退出登录
	@PostMapping("/logout")
	public R<String> logout(HttpServletRequest request) {
		//将登陆时session中保存的id删除
		request.getSession().removeAttribute("employee");
		return R.success("退出成功");
	}
	
	//新增员工
	@PostMapping
	//用对象封装传过来的员工信息参数
	public R<String> save(@RequestBody Employee employee){
		log.info("员工信息为：{}",employee);
		//设置新增员工的默认密码
		employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
		//执行新增员工
		employeeService.save(employee);
		return R.success("添加成功");
	}
	
	//员工分页查询
	@GetMapping("/page")
	public R<Page> page(int page,int pageSize,String name){
		//构造分页过滤器
		Page pageInfo = new Page<>(page, pageSize);
		//构造条件器
		LambdaQueryWrapper<Employee> pageLambdaQueryWrapper = new LambdaQueryWrapper<>();
		//添加条件
		pageLambdaQueryWrapper.like(StringUtils.hasText(name) ,Employee::getName,name);
		//添加排序条件
		pageLambdaQueryWrapper.orderByAsc(Employee::getUpdateTime);
		//执行查询
		employeeService.page(pageInfo,pageLambdaQueryWrapper);
		return R.success(pageInfo);
	}
	
	//修改员工信息
	@PutMapping
	public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
		log.info("id为{}",employee.getId());
		employeeService.updateById(employee);
		return R.success("修改员工信息成功");
	}
	
	//回显
	@GetMapping("/{id}")
	//路径参数
	public R<Employee> select(@PathVariable Long id){
		//根据id回显
		Employee emp = employeeService.getById(id);
		if (emp!=null){
			return R.success(emp);
		}
		return R.error("查询失败");
	}
	
}
