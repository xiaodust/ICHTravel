package com.icht.backfront.service.impl;

import com.icht.backfront.dao.CartItemDAO;
import com.icht.backfront.dataobject.CartItemDO;
import com.icht.backfront.model.CartItem;
import com.icht.backfront.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    private CartItemDAO cartItemDAO;
    @Override
    public int addItem(CartItem cartItem) {
        if (cartItem==null){
            return 0;
        }
        if (cartItem.getCartId()==null){
            return 0;
        }
        if (cartItem.getProductId()==null){
            return 0;
        }
        if (cartItem.getId()==null){
            cartItem.setId(UUID.randomUUID().toString());
        }

        // 先尝试通过productId查找商品
        CartItemDO cartItemDO=cartItemDAO.findByProductId(cartItem.getProductId(),cartItem.getCartId());
        
        // 如果找到了相同productId的商品，但没有相同的productDetailId，那么不合并数量
        // 这样不同规格的同一商品会作为不同的购物车项
        if (cartItemDO!=null && 
            ((cartItem.getProductDetailId() == null && cartItemDO.getProductDetailId() == null) || 
             (cartItem.getProductDetailId() != null && cartItem.getProductDetailId().equals(cartItemDO.getProductDetailId())))) {
            // 只在相同商品规格的情况下合并数量
            cartItemDO.setNumber(cartItem.getNumber()+cartItemDO.getNumber());
            cartItemDO.setTotalPrice(cartItem.getTotalPrice()+cartItemDO.getTotalPrice());
            // 更新productDetailId字段（如果传入了新值）
            if (cartItem.getProductDetailId() != null && !cartItem.getProductDetailId().isEmpty()) {
                cartItemDO.setProductDetailId(cartItem.getProductDetailId());
            }
            return cartItemDAO.update(cartItemDO);
        }
        
        // 如果没有找到相同规格的商品，或者找到了但规格不同，则插入新记录
        cartItem.setGmtCreated(LocalDateTime.now());
        cartItem.setGmtModified(LocalDateTime.now());
        return cartItemDAO.insert(new CartItemDO(cartItem));
    }

    @Override
    public int deleteItem(String id) {
        if (id==null){
            return 0;
        }
        CartItemDO cartItemDO=cartItemDAO.findById(id);
        if (cartItemDO==null){
            return 0;
        }
        return cartItemDAO.delete(cartItemDO);
    }

    @Override
    public List<CartItem> getCartItems(String cartId) {
        if (cartId==null){
            return null;
        }
        List<CartItemDO> cartItemDOList=cartItemDAO.findByCartId(cartId);
        if (cartItemDOList==null){
            return null;
        }
        List<CartItem> cartItemList=new ArrayList<CartItem>();
        for (CartItemDO cartItemDO:cartItemDOList){
            CartItem cartItem=cartItemDO.ToModel();
            cartItemList.add(cartItem);
        }
        return cartItemList;
    }
}
