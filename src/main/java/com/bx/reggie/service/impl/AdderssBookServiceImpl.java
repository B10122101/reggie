package com.bx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bx.reggie.entity.AddressBook;
import com.bx.reggie.mapper.AddressBookMapper;
import com.bx.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author BX
 * @version 1.0
 * @date 2023/8/4 15:29
 */
@Service
public class AdderssBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
