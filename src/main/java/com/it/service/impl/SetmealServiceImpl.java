package com.it.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.common.CustomException;
import com.it.dto.SetmealDto;
import com.it.entity.Setmeal;
import com.it.entity.SetmealDish;
import com.it.mapper.SetmealMapper;
import com.it.service.SetmealDishService;
import com.it.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealDto.getId());
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        int size = ids.size();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,0);
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmeals = this.list(setmealLambdaQueryWrapper);
        if (setmeals.size()!=size){
            throw new CustomException("套餐正在售卖中,不能删除!");
        }
        boolean b = this.remove(setmealLambdaQueryWrapper);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }
}
