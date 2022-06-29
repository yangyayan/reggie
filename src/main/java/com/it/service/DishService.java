package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.dto.DishDto;
import com.it.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    //新增菜品，同时插入菜品对应的口味数据
     void saveWithFlavor(DishDto dishDto);

     DishDto getByIdWithFlavor(Long id);

     void updateWithFlavor(DishDto dishDto);

    void delete(List<String> idsList);
}
