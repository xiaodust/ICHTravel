package com.icht.backfront.dao;

import com.icht.backfront.dataobject.CartItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartItemDAO {
    int insert(CartItemDO cartItem);

    CartItemDO findByProductId(String productId,String cartId);

    int update(CartItemDO cartItemDO);

    int delete(CartItemDO cartItemDO);

    List<CartItemDO> findByCartId(String shoppingCartId);

    CartItemDO findById(String id);



}
