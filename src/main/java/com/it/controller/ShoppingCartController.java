package com.it.controller;

import com.it.common.R;
import com.it.entity.ShoppingCart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @GetMapping("/list")
    public R<ShoppingCart> list(){
        return null;
    }
}
