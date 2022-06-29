package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.dto.DishDto;
import com.it.dto.SetmealDishDto;
import com.it.dto.SetmealDto;
import com.it.entity.*;
import com.it.service.CategoryService;
import com.it.service.DishService;
import com.it.service.SetmealDishService;
import com.it.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequestMapping("/setmeal")
@RestController
public class SetmealController {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){


        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();


        LambdaQueryWrapper<Setmeal> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        dishLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,dishLambdaQueryWrapper);


        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");


        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto>  list= new ArrayList<>();
        for (Setmeal record:records){


            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                setmealDto.setCategoryName(category.getName());
            }
            list.add(setmealDto);
        }
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,String ids){
        String[] split = ids.split(",");
        LambdaUpdateWrapper<Setmeal> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.in(split!=null,Setmeal::getId, Arrays.asList(split));
        dishLambdaUpdateWrapper.set(Setmeal::getStatus,status);

        boolean bool = setmealService.update(dishLambdaUpdateWrapper);
        if (bool){
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {

        setmealService.delete(ids);
        return R.success("删除成功");
    }

    @RequestMapping("/list")
    public R<List<SetmealDto>> getCategoryById(Setmeal setmeal){

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus,1);
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);

        ArrayList<SetmealDto> dishDtos = new ArrayList<>();

        for (Setmeal record:list){

            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record,setmealDto);

            Long id = record.getId();
            List<SetmealDish> dishFlavors = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getDishId, id));
            setmealDto.setSetmealDishes(dishFlavors);

            dishDtos.add(setmealDto);
        }
        return R.success(dishDtos);
    }

    /*@GetMapping("/dish/{setmealId}")
    public R<List<Dish>> dishList(@PathVariable Long setmealId){
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

        ArrayList<Dish> dishes = new ArrayList<>();
        for (SetmealDish setmealDish:setmealDishes){
            Long dishId = setmealDish.getDishId();

            Dish dish = dishService.getById(dishId);
            dishes.add(dish);
        }
        return R.success(dishes);
    }*/

    @GetMapping("/dish/{setmealId}")
    public R<List<SetmealDishDto>> dishList(@PathVariable Long setmealId){
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

        ArrayList<SetmealDishDto> setmealDishDtos = new ArrayList<>();
        for (SetmealDish setmealDish:setmealDishes){
            SetmealDishDto setmealDishDto = new SetmealDishDto();
            Long dishId = setmealDish.getDishId();
            Dish dish = dishService.getById(dishId);

            Integer copies = setmealDish.getCopies();

            BeanUtils.copyProperties(dish,setmealDishDto);
            setmealDishDto.setCopies(copies);

            setmealDishDtos.add(setmealDishDto);
        }
        return R.success(setmealDishDtos);
    }

    @GetMapping("/{setmealId}")
    public R<SetmealDto> showSetmeal(@PathVariable Long setmealId){

        LambdaQueryWrapper<SetmealDish> setmealDtoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDtoLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDtoLambdaQueryWrapper);

        Setmeal setmeal = setmealService.getById(setmealId);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        setmealDto.setSetmealDishes(setmealDishes);

        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealService.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishes){
            Long setmealId = setmealDto.getId();
            setmealDish.setSetmealId(setmealId);



            setmealDishService.save(setmealDish);
        }
        return R.success("修改成功");
    }
}
