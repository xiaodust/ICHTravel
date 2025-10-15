package com.icht.backfront.service.impl;

import com.icht.backfront.dao.ShoppingCartDAO;
import com.icht.backfront.dataobject.ShoppingCartDO;
import com.icht.backfront.model.ShoppingCart;
import com.icht.backfront.service.ShoppingCartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartDAO shoppingCartDAO;
    @Override
    public ShoppingCart getByUserId(String userId) {
        if(StringUtils.isNotBlank(userId)){
            return null;
        }
        ShoppingCartDO shoppingCartDO = shoppingCartDAO.getByUserId(userId);
        if(shoppingCartDO == null){
            return null;
        }
        return shoppingCartDO.ToModel();
    }
}
