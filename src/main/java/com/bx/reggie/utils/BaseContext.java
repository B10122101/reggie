package com.bx.reggie.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 14:55
 *
 * ThreadLocal 是 Java 中的一个类，它提供了线程局部变量的功能。
 * 它可以让你在每个线程中创建一个独立的副本，并且每个线程只能访问自己的副本，而不会影响其他线程的副本。
 * 每个线程都拥有自己独立的变量副本，因此在多线程环境下使用 ThreadLocal 可以保证数据的线程安全性。
 *
 * ThreadLocal 常常用于解决多线程环境下的数据共享问题。它可以在每个线程中保存不同的数据，而不需要使用锁或其他同步机制。
 *
 * 使用 ThreadLocal 的基本步骤如下：
 * 创建一个 ThreadLocal 对象，并指定泛型类型，这个泛型类型即为线程局部变量的类型。
 * 使用 ThreadLocal 的 set() 方法在当前线程中设置数据值。
 * 使用 ThreadLocal 的 get() 方法在当前线程中获取数据值。
 * 在需要的时候，可以使用 ThreadLocal 的 remove() 方法清除当前线程中的数据
 */
@Slf4j
@Component
public class BaseContext {
	//线程局部变量
	private static ThreadLocal<Long> threadLocal = new ThreadLocal();
	
	//往该线程局部变量中设置数据值
	public static void set(Long id) {
		threadLocal.set(id);
	}
	
	//取出数据值
	public static Long get() {
		return threadLocal.get();
	}
	
	public static Long getCurrentId() {
		return threadLocal.get();
	}
}
