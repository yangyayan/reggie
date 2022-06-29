package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {
    void setDefault(AddressBook addressBook);
}
