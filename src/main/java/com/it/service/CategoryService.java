package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.entity.Category;

public interface CategoryService extends IService<Category> {
     void remove(Long id);
}
