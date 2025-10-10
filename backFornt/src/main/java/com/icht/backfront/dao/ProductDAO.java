package com.icht.backfront.dao;

import com.icht.backfront.dataobject.ProductDO;
import com.icht.backfront.param.BasePageParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductDAO {
     List<ProductDO> findAll();
     ProductDO findById(@Param("id") String id);
     int save(ProductDO product);
     int update(ProductDO product);
     int delete(String id);
     List<ProductDO> pageQuery(BasePageParam basePageParam);
     int selectAllCounts();

}