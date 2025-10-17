package com.icht.backfront.api;

import com.icht.backfront.dao.CartItemDAO;
import com.icht.backfront.dao.ShoppingCartDAO;
import com.icht.backfront.dataobject.ShoppingCartDO;
import com.icht.backfront.model.CartItem;
import com.icht.backfront.model.Result;
import com.icht.backfront.model.ShoppingCart;
import com.icht.backfront.service.CartItemService;
import com.icht.backfront.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/cart")
public class CartApi {
    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private CartItemDAO cartItemDAO;
    @Autowired
    private ShoppingCartDAO shoppingCartDAO;

    @PostMapping("/add")
    @ResponseBody
    public Result addCartItem(@RequestBody CartItem cartItem) {
        Result result = new Result();
        result.setSuccess(true);
        cartItemService.addItem(cartItem);
        ShoppingCart shoppingCart=shoppingCartService.getByUserId(cartItem.getCartId());
        if (shoppingCart==null){
            result.setSuccess(false);
            result.setCode("601");
            result.setMessage("购物车不存在");
            return result;
        }
        shoppingCart.setTotalPrice(shoppingCart.getTotalPrice()+cartItem.getTotalPrice());
        shoppingCart.setNumber(shoppingCart.getNumber()+cartItem.getNumber());
;
        if (cartItemDAO.findByProductId(cartItem.getProductId(),cartItem.getCartId())==null){
            List<String> itemId=shoppingCart.getItemId();
            itemId.add(cartItem.getId());
            shoppingCart.setItemId(itemId);
        }
        shoppingCartDAO.updateShoppingCart(new ShoppingCartDO(shoppingCart));
        result.setData(cartItem);
        result.setCode("200");
        result.setMessage("添加成功");
        return result;
    }

    @GetMapping("/{cartId}")
    @ResponseBody
    public Result getCart(@PathVariable String cartId) {
        Result result = new Result();
        result.setSuccess(true);
        result.setData(shoppingCartService.getByUserId(cartId));
        result.setCode("200");
        result.setMessage("获取成功");
       return result;
    }

    @GetMapping("/items/{cartId}")
    @ResponseBody
    public Result getCartItems(@PathVariable String cartId) {
        Result result = new Result();
        result.setSuccess(true);
        result.setData(cartItemService.getCartItems(cartId));
        result.setCode("200");
        result.setMessage("获取成功");
        return result;
    }

    @DeleteMapping("/{cartItemId}")
    @ResponseBody
    public Result deleteCartItem(@PathVariable String cartItemId) {
        Result result = new Result();
        result.setSuccess(true);
        cartItemService.deleteItem(cartItemId);
        ShoppingCart shoppingCart=shoppingCartService.getByUserId(cartItemId);
        shoppingCart.setTotalPrice(shoppingCart.getTotalPrice()-cartItemDAO.findById(cartItemId).getTotalPrice());
        shoppingCart.setNumber(shoppingCart.getNumber()-cartItemDAO.findById(cartItemId).getNumber());
        List<String> itemId=shoppingCart.getItemId();
        itemId.remove(cartItemId);
        shoppingCart.setItemId(itemId);
        result.setData(shoppingCart);
        result.setCode("200");
        result.setMessage("删除成功");
        return result;
    }

}
