package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.Product;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class ProductDO {
    private String id;
    private String name;
    private double price;
    //    商品销量
    private Integer purchaseNum;
    //   商品介绍
    private String productIntro;

    private String productImg;
    
    public ProductDO() {
    }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(Integer purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public String getProductIntro() {
        return productIntro;
    }

    public void setProductIntro(String productIntro) {
        this.productIntro = productIntro;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
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

    public ProductDO(Product product) {

        BeanUtils.copyProperties(product, this);

    }

    public Product ToModel() {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setPurchaseNum(purchaseNum);
        product.setProductIntro(productIntro);
        product.setProductImg(productImg);
        product.setGmtCreated(gmtCreated);
        product.setGmtModified(gmtModified);
        return product;
    }
}
