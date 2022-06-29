package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.dto.DishDto;
import com.it.entity.Category;
import com.it.entity.Dish;
import com.it.entity.DishFlavor;
import com.it.service.CategoryService;
import com.it.service.DishFlavorService;
import com.it.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //Dish表不能查出categoryName字段
        Page<Dish> dishPage = new Page<>(page,pageSize);
        //利用DishDto可以把categoryName存起来
        Page<DishDto> dishDtoPage = new Page<>();

        //按条件查询菜品并且给了dishPage
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name!=null,Dish::getName,name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,dishLambdaQueryWrapper);

        //除了records属性，复制dishPage到dishDtoPage
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        //records中封装的是具体的菜品信息，但是缺少菜品分类
        List<Dish> records = dishPage.getRecords();
        List<DishDto>  list= new ArrayList<>();
        for (Dish record:records){

            //利用菜品id，从菜品分类表得到菜品分类的名字，赋给新的dishDto对象，并且一一添加到list集合，最后赋值给dishDtoPage的records属性
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }
            list.add(dishDto);
        }
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品，同时插入菜品对应的口味数据
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("添加成功");
    }
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable int status,String ids){
        String[] split = ids.split(",");
        LambdaUpdateWrapper<Dish> dishLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        dishLambdaUpdateWrapper.in(split!=null,Dish::getId, Arrays.asList(split));
        dishLambdaUpdateWrapper.set(Dish::getStatus,status);

        boolean bool = dishService.update(dishLambdaUpdateWrapper);
        if (bool){
            return R.success("修改成功");
        }
        return R.error("修改失败");
    }

    @DeleteMapping
    public R<String> delete(String ids){

        String[] split = ids.split(",");
        List<String> idsList = Arrays.asList(split);

        dishService.delete(idsList);

        return R.success("修改成功");

    }

    @RequestMapping("/list")
    public R<List<DishDto>> getCategoryById(Dish dish){

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);

        ArrayList<DishDto> dishDtos = new ArrayList<>();

        for (Dish record:list){

            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(record,dishDto);

            Long id = record.getId();
            List<DishFlavor> dishFlavors = dishFlavorService.list(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
            dishDto.setFlavors(dishFlavors);

            dishDtos.add(dishDto);
        }
        return R.success(dishDtos);
    }
}
