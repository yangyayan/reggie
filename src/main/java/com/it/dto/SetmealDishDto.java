package com.it.dto;

import com.it.entity.Dish;
import lombok.Data;

@Data
public class SetmealDishDto extends Dish {
    private Integer copies;
}
