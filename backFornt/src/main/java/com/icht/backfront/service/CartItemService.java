package com.icht.backfront.service;

import com.icht.backfront.model.CartItem;

import java.util.List;

public interface CartItemService {
    int addItem(CartItem cartItem,String cartId);

    int deleteItem(String id);

    List<CartItem> getCartItems(String cartId);

}
