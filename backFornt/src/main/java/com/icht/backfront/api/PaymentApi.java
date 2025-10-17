package com.icht.backfront.api;

import com.icht.backfront.model.PaymentRecord;
import com.icht.backfront.model.PaymentStatus;
import com.icht.backfront.model.Result;
import com.icht.backfront.param.PaymentParam;
import com.icht.backfront.service.PayService;
import com.icht.backfront.service.PaymentRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/alipay")
public class PaymentApi {
    private static final Logger logger = LoggerFactory.getLogger(PaymentApi.class);
    @Autowired
    private PayService payService;

    @Autowired
    private PaymentRecordService paymentRecordService;

    @PostMapping("/pay")
    @ResponseBody
    public Result payOrder(@RequestBody PaymentParam paymentParam){
        Result result = new Result();
        result.setSuccess(true);

        if (StringUtils.isEmpty(paymentParam.getUserId())){
            result.setSuccess(false);
            result.setMessage("useId is null");
            result.setCode("601");
            return result;
        }

        if (paymentParam.getTotalAmount()<0){
            result.setSuccess(false);
            result.setMessage("支付金额不能小于0");
            result.setCode("602");
            return result;
        }
        PaymentRecord paymentRecord = initPaymentRecord(paymentParam);
        PaymentRecord saveResult = paymentRecordService.save(paymentRecord);
       if (saveResult==null){
           logger.error("支付流水记录插入失败!");
       }
       return payService.payOrder(paymentParam);
    }

    //组装支付记录
    private PaymentRecord initPaymentRecord(PaymentParam paymentParam){
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentType(paymentParam.getPayType());
        paymentRecord.setPaymentStatus(PaymentStatus.PENDING);
        paymentRecord.setUserId(paymentParam.getUserId());
        paymentRecord.setOrderNumber(paymentParam.getOrderNumber());
        paymentRecord.setAmount(paymentParam.getPayAmount());
        return paymentRecord;
    }
}


