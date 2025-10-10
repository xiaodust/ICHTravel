package com.icht.backfront.model;

import com.icht.backfront.param.PayType;

import java.time.LocalDateTime;

public class PaymentRecord {
    private String id;

    private String userId;

    private String orderNumber;
    //外部支付渠道主题id
    private String channelPaymentId;
    //渠道类型
    private String channelType;

    private Double amount;

    private PayType paymentType;

    private PaymentStatus paymentStatus;
    //订单额外信息
    private String extendStr;

    //支付完成时间
    private String payEndTime;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getChannelPaymentId() {
        return channelPaymentId;
    }

    public void setChannelPaymentId(String channelPaymentId) {
        this.channelPaymentId = channelPaymentId;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getExtendStr() {
        return extendStr;
    }

    public void setExtendStr(String extendStr) {
        this.extendStr = extendStr;
    }

    public String getPayEndTime() {
        return payEndTime;
    }

    public void setPayEndTime(String payEndTime) {
        this.payEndTime = payEndTime;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }
    public PayType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PayType paymentType) {
        this.paymentType = paymentType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

