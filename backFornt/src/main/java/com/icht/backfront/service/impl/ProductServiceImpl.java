package com.icht.backfront.service.impl;

import com.icht.backfront.dao.ProductDAO;
import com.icht.backfront.dataobject.ProductDO;
import com.icht.backfront.model.Paging;
import com.icht.backfront.model.Product;
import com.icht.backfront.param.BasePageParam;
import com.icht.backfront.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDAO productDAO;

    @Override
    public int save(Product product) {
          if (StringUtils.isBlank(product.getId())){
              product.setId(UUID.randomUUID().toString());
              product.setGmtCreated(LocalDateTime.now());
              product.setGmtModified(LocalDateTime.now());
              return productDAO.save(new ProductDO(product));
          }
          product.setGmtCreated(LocalDateTime.now());
          product.setGmtModified(LocalDateTime.now());
        return productDAO.save(new ProductDO(product));
    }

    @Override
    public Product getById(String id) {
        if (StringUtils.isBlank(id)){
            return null;
        }
        ProductDO productDO = productDAO.findById(id);
        if (productDO == null) {
            return null;
        }
        return productDO.ToModel();
    }

    @Override
    public Paging<Product> pageQueryProduct(BasePageParam basePageParam) {
        Paging<Product> result=new Paging<>();

        if (basePageParam.getPagination()<0){
            basePageParam.setPagination(0);
        }

        if (basePageParam.getPageSize()<0){
            basePageParam.setPageSize(9);
        }

        int count =productDAO.selectAllCounts();
        if (count<0){
            return  result;
        }

        int totalPage = (int)Math.ceil(count / (basePageParam.getPageSize() * 1.0));
        result.setTotalPage(totalPage);
        result.setTotalCount(count);
        result.setPageNum(basePageParam.getPagination());
        result.setPageSize(basePageParam.getPageSize());

        List<ProductDO> productDOS=productDAO.pageQuery(basePageParam);
        List<Product> productList=new ArrayList<>();
        for (ProductDO productDO:productDOS){
            Product product=productDO.ToModel();
            productList.add(product);
        }
        result.setData(productList);

        return result;
    }
}
