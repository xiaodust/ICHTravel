package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.ShoppingCart;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ShoppingCartDO {
    private String userId;
    private List<String> itemId;
    private int number;
    private int totalPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getItemId() {
        return itemId;
    }

    public void setItemId(List<String> itemId) {
        this.itemId = itemId;
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
    public ShoppingCartDO() {}

    public ShoppingCartDO(ShoppingCart shoppingCart) {
      BeanUtils.copyProperties(shoppingCart,this);
    }

    public ShoppingCart ToModel(){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(this.getUserId());
        shoppingCart.setItemId(this.getItemId());
        shoppingCart.setNumber(this.getNumber());
        shoppingCart.setTotalPrice(this.getTotalPrice());
        shoppingCart.setGmtCreated(this.getGmtCreated());
        shoppingCart.setGmtModified(this.getGmtModified());
        return shoppingCart;
    }

}
