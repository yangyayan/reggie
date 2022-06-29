package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.dto.SetmealDto;
import com.it.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void delete(List<Long> ids);
}
