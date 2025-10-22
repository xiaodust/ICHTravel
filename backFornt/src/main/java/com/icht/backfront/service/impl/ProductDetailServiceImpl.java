package com.icht.backfront.service.impl;

import com.icht.backfront.dao.ProductDetailDAO;
import com.icht.backfront.dataobject.ProductDetailDO;
import com.icht.backfront.model.ProductDetail;
import com.icht.backfront.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {
    @Autowired
    private ProductDetailDAO productDetailDAO;


    @Override
    public List<ProductDetail> queryProductDetail(List<String> productDetailIds) {
        if (StringUtils.isEmpty(productDetailIds)) {
            return null;
        }
        List<ProductDetailDO> productDetailDOS=productDetailDAO.selectByIds(productDetailIds);
        List<ProductDetail> productDetailList=new ArrayList<ProductDetail>();
        if (CollectionUtils.isEmpty(productDetailDOS)) {
            return productDetailList;
        }

        for (ProductDetailDO productDetailDO : productDetailDOS) {
            ProductDetail productDetail=productDetailDO.TOModel();
            productDetailList.add(productDetail);
        }
        return productDetailList;
    }

    @Override
    public ProductDetail save(ProductDetail productDetail) {
        if (StringUtils.isEmpty(productDetail.getId())) {
            productDetail.setId(UUID.randomUUID().toString());
            productDetail.setGmtCreated(LocalDateTime.now());
            productDetail.setGmtModified(LocalDateTime.now());
            productDetailDAO.insert(new ProductDetailDO(productDetail));

            return productDetail;
        }
        productDetailDAO.updateById(new ProductDetailDO(productDetail));
        return productDetail;
    }

    @Override
    public List<ProductDetail> getByProductId(String productId) {
      if (StringUtils.isEmpty(productId)) {
          return null;
      }
      List<ProductDetailDO> productDetailDOS=productDetailDAO.selectByProductId(productId);
      List<ProductDetail> productDetails=new ArrayList<>();
      if (CollectionUtils.isEmpty(productDetailDOS)) {
          return productDetails;
      }
      for (ProductDetailDO productDetailDO : productDetailDOS) {
          ProductDetail productDetail=productDetailDO.TOModel();
          productDetails.add(productDetail);
      }
      return productDetails;
    }

    @Override
    public ProductDetail get(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        ProductDetailDO productDetailDO=productDetailDAO.selectById(id);
        if (productDetailDO == null) {
            return null;
        }
        return productDetailDO.TOModel();
    }
}
