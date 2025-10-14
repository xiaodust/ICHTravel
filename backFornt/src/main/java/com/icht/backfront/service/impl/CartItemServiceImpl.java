package com.icht.backfront.service.impl;

import com.icht.backfront.dao.CartItemDAO;
import com.icht.backfront.dataobject.CartItemDO;
import com.icht.backfront.model.CartItem;
import com.icht.backfront.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartItemServiceImpl implements CartItemService {
    @Autowired
    private CartItemDAO cartItemDAO;
    @Override
    public int addItem(CartItem cartItem,String cartId) {
        if (cartId==null){
            return 0;
        }
        if (cartItem==null){
            return 0;
        }
        if (cartItem.getProductId()==null){
            return 0;
        }

        CartItemDO cartItemDO=cartItemDAO.findByProductId(cartItem.getProductId(),cartId);
        if (cartItemDO==null){
            cartItemDO.setNumber(cartItem.getNumber()+cartItemDO.getNumber());
           return cartItemDAO.update(cartItemDO);
        }
        cartItemDO=new CartItemDO(cartItem);
        return cartItemDAO.insert(cartItemDO);
    }

    @Override
    public int deleteItem(String itemId) {
        if (itemId==null){
            return 0;
        }
        CartItemDO cartItemDO=cartItemDAO.findById(itemId);
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
