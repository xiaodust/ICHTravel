package com.icht.backfront.dataobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icht.backfront.model.ProductDetail;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

public class ProductDetailDO {
    private String id;

    private String productId;

    private Double price;

    private Double size;

    private Integer stock;

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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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

    public ProductDetailDO(){

    }

    public ProductDetailDO(ProductDetail productDetail){
        BeanUtils.copyProperties(productDetail, this);
    }

    public ProductDetail TOModel(){
        ProductDetail productDetail = new ProductDetail();
        productDetail.setId(id);
        productDetail.setProductId(productId);
        productDetail.setPrice(price);
        productDetail.setSize(size);
        productDetail.setStock(stock);
        productDetail.setGmtCreated(gmtCreated);
        productDetail.setGmtModified(gmtModified);
        return productDetail;
    }
}
