package com.icht.backfront.service;

import com.icht.backfront.model.ShoppingCart;

public interface ShoppingCartService {
    ShoppingCart getByUserId(String userId);

}
