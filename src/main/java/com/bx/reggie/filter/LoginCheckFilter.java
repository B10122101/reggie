package com.bx.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.bx.reggie.utils.BaseContext;
import com.bx.reggie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/1 11:35
 */
@SuppressWarnings({"all"})
//过滤器注解(名字，拦截路径)
@WebFilter(filterName = "LonginCheckFilter", urlPatterns = "/*")
@Slf4j

public class LoginCheckFilter implements Filter {
	//用来匹配路径是否属于大路径中包含的
	private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
	
	@Override
	/**
	 * 登录拦截器思想(这里使用web中的过滤器)
	 * 自定义的拦截过滤器要继承过滤器，
	 * 并在该过滤器上使用@WebFilter(名字，拦截路径)注解配置要拦截的请求和启动类上加@ServletComponentScan开启Servlet组件支持
	 * 1、获取每次请求的路径
	 * 2、判断哪些路径需要拦截，不需要拦截的直接放行
	 * 3、需要拦截的路径判断是否登录
	 * 4、已登录就放行，未登录就不放行并响应给前端
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		//获取本次请求的路径
		String requestURI = request.getRequestURI();
		if (!requestURI.equals("/common/download")) {
			log.info("{}", requestURI);
		}
		//定义这些请求不需要要拦截
		String[] urls = new String[]{
				"/employee/login",
				"/employee/logout",
				"/backend/**",
				"/front/**",
				"/user/sendMsg",
				"/user/login"
		};
		
		//判断是否需要拦截该请求
		boolean check = check(urls, requestURI);
		
		//不需要直接放行
		if (check) {
			log.info("本次请求{}不需要拦截", requestURI);
			//放行
			filterChain.doFilter(request, response);
			return;
		}
		
		//需要拦截，判断是否登录，已经登录直接放行
		if (request.getSession().getAttribute("employee") != null) {
			Long emp = (Long) request.getSession().getAttribute("employee");
			BaseContext.set(emp);
			filterChain.doFilter(request, response);
			return;
		}
		
		
		if (request.getSession().getAttribute("user") != null) {
//			log.info("用户已登录，用户ID为:{}",request.getSession().getAttribute("user"));
			Long userId = (Long) request.getSession().getAttribute("user");
			BaseContext.set(userId);
			filterChain.doFilter(request, response);
			return;
		}
		log.info("用户未登录");
		//未登录，响应给前端"NOTLOGIN"
		response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
		return;
	}
	
	
	//此次请求是否属于不需要要拦截的请求
	public boolean check(String[] urls, String requestURI) {
		for (String url : urls) {
			boolean match = PATH_MATCHER.match(url, requestURI);
			if (match) {
				return true;
			}
		}
		return false;
	}
}
