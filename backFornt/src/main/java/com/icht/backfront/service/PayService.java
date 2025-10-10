package com.icht.backfront.service;

import com.icht.backfront.model.Result;
import com.icht.backfront.param.PaymentParam;

import java.util.Map;

public interface PayService {
    Result payOrder(PaymentParam paymentParam);

    Result alipayCallback(Map<String, String> map);

}
