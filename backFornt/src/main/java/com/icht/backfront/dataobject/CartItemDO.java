package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.CartItem;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class CartItemDO {
    private String id;

    private String cartId;

    private String productId;

    private int number;

    private String productName;

    private int productPrice;

    private int totalPrice;

    private String productImage;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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

    public CartItemDO() {}

    public CartItemDO(CartItem cartItem) {
        BeanUtils.copyProperties(cartItem,this);
    }

    public CartItem ToModel() {
        CartItem cartItem = new CartItem();
        cartItem.setId(this.id);
        cartItem.setCartId(this.cartId);
        cartItem.setProductId(this.productId);
        cartItem.setNumber(this.number);
        cartItem.setProductName(this.productName);
        cartItem.setProductPrice(this.productPrice);
        cartItem.setTotalPrice(this.totalPrice);
        cartItem.setProductImage(this.productImage);
        cartItem.setGmtCreated(this.gmtCreated);
        cartItem.setGmtModified(this.gmtModified);
        return cartItem;
    }
}
