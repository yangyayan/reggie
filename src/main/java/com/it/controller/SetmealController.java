package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.dto.DishDto;
import com.it.dto.SetmealDto;
import com.it.entity.Category;
import com.it.entity.Dish;
import com.it.entity.Setmeal;
import com.it.service.CategoryService;
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
}
