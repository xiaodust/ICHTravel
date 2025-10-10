package com.icht.backfront.service;

import com.icht.backfront.model.ProductDetail;

import java.util.List;

public interface ProductDetailService {
    public List<ProductDetail> queryProductDetail(List<String> productDetailIds);

    ProductDetail save(ProductDetail productDetail);

    List<ProductDetail> getByProductId(String productId);

    ProductDetail get(String id);
}
