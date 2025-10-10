package com.icht.backfront.service.impl;

import com.icht.backfront.dao.OrderDAO;
import com.icht.backfront.dataobject.OrderDO;
import com.icht.backfront.model.*;
import com.icht.backfront.service.OrderService;
import com.icht.backfront.service.ProductDetailService;
import com.icht.backfront.service.ProductService;
import com.icht.backfront.service.UserService;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private ProductDetailService productDetailService;
    @Autowired
    private RedissonClient redisson;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;



    @Override
    public Order add(Order order) {
        if (order==null){
            return null;
        }
        if (StringUtils.isEmpty(order.getId())) {
            order.setId(UUID.randomUUID().toString());
        }
        ProductDetail productDetail=productDetailService.get(order.getProductDetailId());
        if (productDetail==null){
            return null;
        }
        order.setTotalPrice(productDetail.getPrice());
        order.setId(UUID.randomUUID().toString());
        order.setOrderStatus(OrderStatus.WAIT_BUYER_PAY);
        order.setOrderNumber(createOrderNumber());
        OrderDO orderDO=new OrderDO(order);
        int insert = orderDAO.insert(orderDO);
        if (insert==1){
            return order;
        }
        return null;
    }

    @Override
    public Order getByOrderNumber(String orderNumber) {
        if (StringUtils.isEmpty(orderNumber)) {
            return null;
        }
        OrderDO orderDO=orderDAO.selectByOrderNumber(orderNumber);
        if (orderDO==null){
            return null;
        }
        return orderDO.TOModel();
    }

    @Override
    public Order updateOrderStatus(String orderNumber, OrderStatus orderStatus) {
        OrderDO orderDO=orderDAO.selectByOrderNumber(orderNumber);
        if (orderDO==null){
            return null;
        }
        orderDO.setStatus(orderStatus.toString());
        orderDAO.update(orderDO);
        return orderDO.TOModel();
    }

    @Override
    public Order updateProductPersonNumber(String orderNumber) {
        OrderDO orderDO=orderDAO.selectByOrderNumber(orderNumber);
        if (orderDO==null){
            return null;
        }
        RLock transferLock=redisson.getLock("PURCHASE");
        transferLock.lock();

        try{
            ProductDetail productDetail=productDetailService.get(orderDO.getProductDetailId());
            if (productDetail==null){
                return null;
            }
            productDetail.setStock(productDetail.getStock()-1);
            productDetailService.save(productDetail);
            Product product= productService.getById(productDetail.getProductId());
            product.setPurchaseNum(product.getPurchaseNum()+1);
            productService.save(product);
        } catch (Exception e){
             logger.error("",e);
        }finally {
            transferLock.unlock();
        }
        return orderDO.TOModel();
    }

    private String createOrderNumber() {
        LocalDateTime localDateTime=LocalDateTime.now();
        DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String date=dtf.format(localDateTime);
        RAtomicLong aLong =redisson.getAtomicLong("myOrderPartNum" + date);
        aLong.expire(10, TimeUnit.SECONDS);
        long count = aLong.incrementAndGet();
        String orderId=date+count;
        return orderId;
    }
}
