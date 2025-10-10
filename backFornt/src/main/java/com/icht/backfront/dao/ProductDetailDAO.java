package com.icht.backfront.dao;

import com.icht.backfront.dataobject.ProductDetailDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductDetailDAO {
       List<ProductDetailDO> selectByIds(List<String> ids);

       int deleteById(String id);

       int insert(ProductDetailDO productDetailDO);

       ProductDetailDO selectById(String id);

       List<ProductDetailDO> selectAll();

       List<ProductDetailDO>selectByProductId(String productId);

       int updateById(ProductDetailDO productDetailDO);

}
