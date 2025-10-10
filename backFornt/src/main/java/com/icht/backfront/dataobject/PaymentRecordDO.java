package com.icht.backfront.dataobject;

import com.icht.backfront.model.PaymentRecord;
import com.icht.backfront.model.PaymentStatus;
import com.icht.backfront.param.PayType;

import java.time.LocalDateTime;

public class PaymentRecordDO {
    private String id;

    private String userId;

    private String orderNumber;
    //外部支付渠道主题id
    private String channelPaymentId;
    //渠道类型
    private String channelType;

    private String paymentStatus;

    private Double amount;

    private String payType;
    //订单额外信息
    private String extendStr;

    //支付完成时间
    private String payEndTime;

    private LocalDateTime gmtCreated;

    private LocalDateTime gmtModified;

    public PaymentRecordDO(){}

    public PaymentRecordDO(PaymentRecord paymentRecord){
         this.setId(paymentRecord.getId());
         this.setUserId(paymentRecord.getUserId());
         this.setOrderNumber(paymentRecord.getOrderNumber());
         this.setChannelPaymentId(paymentRecord.getChannelPaymentId());
         this.setChannelType(paymentRecord.getChannelType());
         this.setPaymentStatus(paymentRecord.getPaymentStatus().toString());
         this.setPayType(paymentRecord.getPaymentType().toString());
         this.setAmount(paymentRecord.getAmount());
         this.setPayEndTime(paymentRecord.getPayEndTime());
         this.setGmtCreated(paymentRecord.getGmtCreated());
         this.setGmtModified(paymentRecord.getGmtModified());
    }

    public PaymentRecord TOModel(){
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setId(id);
        paymentRecord.setUserId(userId);
        paymentRecord.setOrderNumber(orderNumber);
        paymentRecord.setChannelPaymentId(channelPaymentId);
        paymentRecord.setChannelType(channelType);
        paymentRecord.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
        paymentRecord.setPaymentType(PayType.valueOf(payType));
        paymentRecord.setExtendStr(extendStr);
        paymentRecord.setPayEndTime(payEndTime);
        paymentRecord.setGmtCreated(gmtCreated);
        paymentRecord.setGmtModified(gmtModified);
        return paymentRecord;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
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

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
