package com.icht.backfront.service;

import com.icht.backfront.model.Order;
import com.icht.backfront.model.OrderStatus;
import com.icht.backfront.model.Paging;
import com.icht.backfront.param.QueryOrderParam;

public interface OrderService {
    public Order add(Order order);

    Order getByOrderNumber(String orderNumber);

    Order updateOrderStatus(String orderNumber, OrderStatus orderStatus);

    Order updateProductPersonNumber(String orderNumber);
}
