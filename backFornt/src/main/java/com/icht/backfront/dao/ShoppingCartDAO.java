package com.icht.backfront.dao;

import com.icht.backfront.dataobject.ShoppingCartDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartDAO {
    ShoppingCartDO getByUserId(String userId);

    int updateShoppingCart(ShoppingCartDO shoppingCartDO);

    int insertShoppingCart(ShoppingCartDO shoppingCartDO);

}
