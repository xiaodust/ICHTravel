package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.Order;
import com.icht.backfront.model.OrderStatus;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class OrderDO {
    private String id;

    private String orderNumber;

    private String userId;

    private String productDetailId;

    private Double totalPrice;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductDetailId() {
        return productDetailId;
    }

    public void setProductDetailId(String productDetailId) {
        this.productDetailId = productDetailId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public OrderDO(){}

    public OrderDO(Order order){
      this.setId(order.getId());
      this.setOrderNumber(order.getOrderNumber());
      this.setUserId(order.getUserId());
      this.setProductDetailId(order.getProductDetailId());
      this.setTotalPrice(order.getTotalPrice());
      this.setStatus(order.getOrderStatus().toString());
      this.setGmtCreated(order.getGmtCreated());
      this.setGmtModified(order.getGmtModified());
    }

    public Order TOModel(){
        Order order = new Order();
        order.setId(id);
        order.setOrderNumber(orderNumber);
        order.setUserId(userId);
        order.setProductDetailId(productDetailId);
        order.setTotalPrice(totalPrice);
        if (!StringUtils.isEmpty(status)){
            order.setOrderStatus(OrderStatus.valueOf(status));
        }
        order.setGmtCreated(gmtCreated);
        order.setGmtModified(gmtModified);
        return order;
    }
}
