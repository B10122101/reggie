package com.bx.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/1 9:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements Serializable {
	private static final long seriaVersionUID = 1L;
	private Long id;
	private String username;
	private String name;
	private String password;
	private String phone;
	private String sex;
	private String idNumber;
	private Integer status;
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
	@TableField(fill = FieldFill.INSERT)
	private Long createUser;
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private Long updateUser;
}
