package com.icht.backfront.service;

import com.icht.backfront.model.ShoppingCart;

public interface ShoppingCartService {

    int insertShoppingCart(String userId);

    ShoppingCart getByUserId(String userId);


}
