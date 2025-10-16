package com.icht.backfront.service.impl;

import com.icht.backfront.dao.CartItemDAO;
import com.icht.backfront.dataobject.CartItemDO;
import com.icht.backfront.model.CartItem;
import com.icht.backfront.service.CartItemService;
import com.icht.backfront.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        CartItemDO cartItemDO=cartItemDAO.findByProductId(cartItem.getProductId(),cartItem.getCartId());
        if (cartItemDO!=null){
            cartItemDO.setNumber(cartItem.getNumber()+cartItemDO.getNumber());
            cartItemDO.setTotalPrice(cartItem.getTotalPrice()+cartItemDO.getTotalPrice());
            return cartItemDAO.update(cartItemDO);
        }
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
