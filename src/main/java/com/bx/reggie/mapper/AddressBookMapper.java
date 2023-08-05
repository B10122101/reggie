package com.bx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bx.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 15:29
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
