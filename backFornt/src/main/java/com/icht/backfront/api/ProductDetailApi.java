package com.icht.backfront.api;

import com.icht.backfront.model.ProductDetail;
import com.icht.backfront.model.Result;
import com.icht.backfront.service.ProductDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/productdetail")
public class ProductDetailApi {
    @Autowired
    private ProductDetailService productDetailService;

    @GetMapping("get")
    @ResponseBody
    @CrossOrigin
    public Result<List<ProductDetail>> get(@RequestParam("productId") String productId) {
        Result<List<ProductDetail>> result = new Result<>();

        List<ProductDetail> productDetails=productDetailService.getByProductId(productId);
        result.setData(productDetails);
        result.setSuccess(true);
        result.setCode("200");
        result.setMessage("获取成功");
        return result;

    }

}
