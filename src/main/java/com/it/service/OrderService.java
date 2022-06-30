package com.it.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
