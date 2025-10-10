package com.icht.backfront.model;

public enum PaymentStatus {
    //支付中
    PENDING,

    SUCCESS,

    FAILURE,

    //退款成功
    REFUND_SUCCESS,
    //退款失败
    REFUND_FAILURE,
}
