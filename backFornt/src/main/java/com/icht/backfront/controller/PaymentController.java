package com.icht.backfront.controller;

import com.icht.backfront.model.Result;
import com.icht.backfront.service.PayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/control/pay")
public class PaymentController {
    private final static Logger logger =LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PayService payService;

    @GetMapping("/alipayreturnurl")
    public String alipayreturnurl() {
        return "alipayurl";
    }

    @PostMapping("/alipaycallback")
    void alipaycallback(HttpServletRequest request, HttpServletResponse response) {
        Map<String,String> params=new HashMap<>();
        Map requstParams = request.getParameterMap();
        for (Iterator iter = requstParams.keySet().iterator(); iter.hasNext();){
            String name = (String)iter.next();
            String[] values = (String[])requstParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        Result payResult = payService.alipayCallback(params);
        try {
            if (payResult.isSuccess()) {
                response.getWriter().print("success");
                response.getWriter().flush();
            }
        } catch (IOException e) {
            logger.error("", e);
        }

    }
}
