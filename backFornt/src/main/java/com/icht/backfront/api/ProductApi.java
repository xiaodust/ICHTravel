package com.icht.backfront.api;

import com.icht.backfront.model.Paging;
import com.icht.backfront.model.Product;
import com.icht.backfront.model.Result;
import com.icht.backfront.param.BasePageParam;
import com.icht.backfront.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/product")
@CrossOrigin
public class ProductApi {
    @Autowired
    private ProductService productService;

    @GetMapping("/page")
    @ResponseBody
    public Result<Paging<Product>> page(BasePageParam basePageParam) {
        Result<Paging<Product>> result = new Result<>();

        result.setSuccess(true);
        result.setData(productService.pageQueryProduct(basePageParam));
        result.setMessage("获取成功");
        result.setCode("200");
        return result;
    }

    @GetMapping("/get")
    @ResponseBody
    @CrossOrigin
    public Result<Product> get(@RequestParam("productId") String productId) {

        Result<Product> result = new Result<>();

        result.setSuccess(true);
        result.setData(productService.getById(productId));
        result.setCode("200");
        result.setMessage("获取成功");
        return result;
    }
}
