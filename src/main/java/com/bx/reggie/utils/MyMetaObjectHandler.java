package com.bx.reggie.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/2 12:02
 *
 * MetaObjectHandler是MyBatis-Plus框架中的一个接口，用于处理实体对象在插入和更新时的自动填充功能。
 * 它是MyBatis-Plus提供的一个扩展点，可以方便地对实体对象的指定字段（通常是公有的字段）进行自动填充，而不需要手动设置这些字段的值。
 *
 * 在MyBatis-Plus中，如果一个实体类中的字段需要在插入和更新时自动填充，可以通过实现MetaObjectHandler接口来实现。
 * 该接口有两个核心方法：
 * insertFill: 在执行插入操作时，会调用该方法，用于填充实体对象的字段值。比如可以设置创建时间和创建人等字段。
 * updateFill: 在执行更新操作时，会调用该方法，用于填充实体对象的字段值。比如可以设置更新时间和更新人等字段。
 * 通过实现 MetaObjectHandler 接口并重写这两个方法，可以灵活地对实体对象的字段进行填充。
 * 在插入或更新操作时，MyBatis-Plus 会自动调用这些方法来填充相应字段的值。
 *

 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
	//插入
	@Override
	public void insertFill(MetaObject metaObject) {
		metaObject.setValue("createTime", LocalDateTime.now());
		//取出线程局部变量中设置的数据(登录用户的id)
		metaObject.setValue("createUser", BaseContext.get());
		metaObject.setValue("updateTime", LocalDateTime.now());
		metaObject.setValue("updateUser", BaseContext.get());
	}

	//更新
	@Override
	public void updateFill(MetaObject metaObject) {
		metaObject.setValue("updateTime", LocalDateTime.now());
		//取出线程局部变量中设置的数据(登录用户的id)
		metaObject.setValue("updateUser", BaseContext.get());
	}
}
