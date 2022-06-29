package com.it.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.common.CustomException;
import com.it.dto.DishDto;
import com.it.entity.Dish;
import com.it.entity.DishFlavor;
import com.it.mapper.DishMapper;
import com.it.service.DishFlavorService;
import com.it.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        this.save(dishDto);

        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        Iterator<DishFlavor> iterator = flavors.iterator();
        while (iterator.hasNext()){
            DishFlavor flavor = iterator.next();
            flavor.setDishId(dishDto.getId());
            if (flavor.getName()==null || flavor.getName().equals("")){
                System.out.println("-------------------------------");
                iterator.remove();
            }
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //根据id查询出的dish没有dishFlavor
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long dishId = dish.getId();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
        List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);

        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();
        Iterator<DishFlavor> iterator = flavors.iterator();
        while (iterator.hasNext()){
            DishFlavor flavor = iterator.next();
            flavor.setDishId(dishDto.getId());
            if (flavor.getName()==null || flavor.getName().equals("")){
                System.out.println("-------------------------------");
                iterator.remove();
            }
        }
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    @Transactional
    public void delete(List<String> idsList) {
        int size = idsList.size();
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getStatus,0);
        dishLambdaQueryWrapper.in(Dish::getId,idsList);
        List<Dish> dishes = this.list(dishLambdaQueryWrapper);
        if (dishes.size()!=size){
            throw new CustomException("菜品正在售卖中,不能删除!");
        }
        this.remove(dishLambdaQueryWrapper);

        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId,idsList);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

    }
}
