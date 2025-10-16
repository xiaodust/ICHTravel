package com.icht.backfront.api;

import com.icht.backfront.model.CartItem;
import com.icht.backfront.model.Result;
import com.icht.backfront.service.CartItemService;
import com.icht.backfront.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/cart")
public class CartApi {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ResponseBody
    public Result addCartItem(@RequestBody CartItem cartItem) {
        Result result = new Result();
        result.setSuccess(true);
        cartItemService.addItem(cartItem);
        result.setData(cartItem);
        return result;
    }

    @GetMapping("/{cartId}")
    @ResponseBody
    public Result getCartItem(@PathVariable String cartId) {
        Result result = new Result();
        result.setSuccess(true);
        result.setData(shoppingCartService.getByUserId(cartId));
       return result;
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseBody
    public Result deleteCartItem(@PathVariable String cartItemId) {
        Result result = new Result();
        result.setSuccess(true);
        cartItemService.deleteItem(cartItemId);
        return result;
    }

}
