package com.icht.backfront.service.impl;

import com.icht.backfront.dao.ShoppingCartDAO;
import com.icht.backfront.dataobject.ShoppingCartDO;
import com.icht.backfront.model.ShoppingCart;
import com.icht.backfront.service.ShoppingCartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartDAO shoppingCartDAO;

    @Override
    public int insertShoppingCart(String userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        shoppingCart.setTotalPrice(0);
        shoppingCart.setNumber(0);
        List<String> itemIds=new ArrayList<String>();
        shoppingCart.setItemId(itemIds);
        return shoppingCartDAO.insertShoppingCart(new ShoppingCartDO(shoppingCart));
    }

    @Override
    public ShoppingCart getByUserId(String userId) {
        if(StringUtils.isBlank(userId)){
            return null;
        }
        ShoppingCartDO shoppingCartDO = shoppingCartDAO.getByUserId(userId);
        if(shoppingCartDO == null){
            return null;
        }
        return shoppingCartDO.ToModel();
    }


}
