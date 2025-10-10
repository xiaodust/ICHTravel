package com.icht.backfront.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icht.backfront.model.*;
import com.icht.backfront.param.PayType;
import com.icht.backfront.param.PaymentParam;
import com.icht.backfront.param.PaymentRecordQueryParam;
import com.icht.backfront.service.OrderService;
import com.icht.backfront.service.PayService;
import com.icht.backfront.service.PaymentRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {
     private final static Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentRecordService paymentRecordService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${alipay.app.id:}")
    private String aliPayAppId;

    @Value("${alipay.app.privatekey:}")
    private String aliPayAppPrivateKey;

    @Value("${alipay.publickey:}")
    private String aliPayPublicKey;


    public Result aliPay(String orderId, PaymentParam paymentParam){
        Result result=new Result();
        result.setSuccess(true);

        Order order=orderService.getByOrderNumber(orderId);
        if (order==null){
            result.setSuccess(false);
            result.setMessage("未查询到订单信息");
            return result;
        }

        AlipayClient alipayClient=new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", this.aliPayAppId,
                this.aliPayAppPrivateKey, "json", "UTF-8",
                this.aliPayPublicKey, "RSA2");

        AlipayTradeWapPayRequest alipayRequest=getAlipayTradeWapPayRequest(orderId,paymentParam);

        AlipayTradeWapPayResponse response=null;
        try {
            response=alipayClient.pageExecute(alipayRequest);
        } catch (AlipayApiException e) {
            logger.error("e is ",e);
        }
        if (response.isSuccess()){
            String channelId =response.getTradeNo();
            updatePayRecord(null,channelId, PayType.ALIPAY.toString(),paymentParam.getOrderNumber(), PaymentStatus.PENDING);
        }
        return result;
    }
    @Override
    public Result payOrder(PaymentParam paymentParam) {
       Result result=new Result();
       switch (paymentParam.getPayType()){
           case ALIPAY:
               result=this.aliPay(paymentParam.getOrderNumber(),paymentParam);
               break;
           default:
               break;
       }
        return result;
    }

    @Override
    public Result alipayCallback(Map<String, String> map) {
        Result result=new Result();
        result.setSuccess(true);
        String status =map.get("trade_status");
        String orderNumber = map.get("out_trade_no");
        Order order=orderService.getByOrderNumber(orderNumber);
        String endTime = map.get("gmt_close");
        if (order!=null){
            if ("TRADE_SUCCESS".equals(status)){
                orderService.updateOrderStatus(orderNumber, OrderStatus.TRADE_PAID_SUCCESS);

                PaymentRecordQueryParam queryParam=new PaymentRecordQueryParam();
                queryParam.setOrderNumber(orderNumber);
                List<PaymentRecord> list=paymentRecordService.query(queryParam);
                if (!CollectionUtils.isEmpty(list)){
                    PaymentRecord paymentRecord=list.get(0);
                    paymentRecord.setPaymentStatus(PaymentStatus.SUCCESS);
                    paymentRecordService.updateStatus(paymentRecord);
                }
                orderService.updateProductPersonNumber(orderNumber);
            }
            if ("TRADE_CLOSE".equals(status)){
                orderService.updateOrderStatus(orderNumber,OrderStatus.TRADE_PAID_FAIL);

                PaymentRecordQueryParam queryParam=new PaymentRecordQueryParam();
                queryParam.setOrderNumber(orderNumber);
                List<PaymentRecord> list=paymentRecordService.query(queryParam);
                if (!CollectionUtils.isEmpty(list)){
                    PaymentRecord paymentRecord=list.get(0);
                    paymentRecord.setPaymentStatus(PaymentStatus.FAILURE);
                    paymentRecordService.updateStatus(paymentRecord);
                }
            }
        }
        return result;
    }
    private AlipayTradeWapPayRequest getAlipayTradeWapPayRequest(String orderId,PaymentParam paymentParam){
        Map<String,Object> bizContentMap=new HashMap<>();
        AlipayTradeWapPayRequest request=new AlipayTradeWapPayRequest();
        request.setApiVersion("1.0");
        request.setReturnUrl("");
        request.setNotifyUrl("");
        bizContentMap.put("out_trade_no",orderId);
        bizContentMap.put("total_amount",paymentParam.getTotalAmount());
        bizContentMap.put("quit_url","");
        bizContentMap.put("product_code","QUICK_WAP_WAY");

        try {
            request.setBizContent(objectMapper.writeValueAsString(bizContentMap));
        } catch (JsonProcessingException e) {
            logger.error("e is:", e);
        }
        return request;
    }
    private void updatePayRecord(String endTime, String channelId, String channelType, String orderNumber, PaymentStatus paymentStatus){
        PaymentRecordQueryParam param=new PaymentRecordQueryParam();
        param.setOrderNumber(orderNumber);
        List<PaymentRecord> list=paymentRecordService.query(param);
        if (!CollectionUtils.isEmpty(list)){
            PaymentRecord paymentRecord=list.get(0);
            paymentRecord.setPaymentStatus(paymentStatus);
            if (!StringUtils.isEmpty(endTime)){
                paymentRecord.setPayEndTime(endTime);
            }
        if (!StringUtils.isEmpty(channelId)){
            paymentRecord.setChannelPaymentId(channelId);
        }
        if (!StringUtils.isEmpty(channelType)){
            paymentRecord.setChannelType(channelType);
        }
        PaymentRecord paymentRecordResult=paymentRecordService.save(paymentRecord);
        if (paymentRecordResult==null){
            logger.error("更新支付记录失败！"+"paymentRecordId is"+paymentRecord.getId());
        }
        }

    }
}


