package com.icht.backfront.api;

import com.icht.backfront.model.*;
import com.icht.backfront.service.OrderService;
import com.icht.backfront.service.ProductDetailService;
import com.icht.backfront.service.ProductService;
import com.icht.backfront.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/order")
public class OrderApi {
    private final  static Logger logger= LoggerFactory.getLogger(OrderApi.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductDetailService productDetailService;
    @Autowired
    private ProductService productService;
    @Autowired
    KafkaTemplate<String,Notify> kafkaTemplate;

    @PostMapping("/add")
    @ResponseBody
    public Result<Order> addOrder(@RequestBody Order order, HttpServletRequest request) {
        Result result=new Result();
        result.setSuccess(true);
        if (order==null || StringUtils.isEmpty(order.getProductDetailId())) {
            result.setSuccess(false);
            result.setMessage("order is null or productDetailId is null");
            return result;
        }

        String userId=request.getParameter("userId");
        if (StringUtils.isEmpty(userId)) {
            result.setSuccess(false);
            result.setMessage("无登录信息");
            return result;
        }

        order.setUserId(userId);
        Order orderResult = orderService.add(order);
        result.setData(orderResult);
        return result;
    }

}
