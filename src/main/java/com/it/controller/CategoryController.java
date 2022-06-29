package com.it.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.common.R;
import com.it.entity.Category;
import com.it.entity.Employee;
import com.it.service.impl.CategoryServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryServiceImpl categoryService;
    @GetMapping("/page")
    public R<Page<Category>> page(int  page, int pageSize){

        Page<Category> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Category::getIsDeleted,0);
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    @PostMapping
    public R<String> save(@RequestBody Category category){
        boolean save = categoryService.save(category);
        if (save){
            return R.success("添加成功");
        }
        return R.error("未知错误");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids") Long id){
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> categoryList = categoryService.list(categoryLambdaQueryWrapper);

        return R.success(categoryList);
    }
}
