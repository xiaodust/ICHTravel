package com.icht.backfront.service;

import com.icht.backfront.model.Paging;
import com.icht.backfront.model.Product;
import com.icht.backfront.param.BasePageParam;

public interface ProductService {
    public int save(Product product);

    public Product getById(String id);


    public Paging<Product> pageQueryProduct(BasePageParam basePageParam);
}
