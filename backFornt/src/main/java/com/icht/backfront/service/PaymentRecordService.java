package com.icht.backfront.service;

import com.icht.backfront.model.PaymentRecord;
import com.icht.backfront.param.PaymentRecordQueryParam;

import java.util.List;

public interface PaymentRecordService {
    PaymentRecord save(PaymentRecord paymentRecord);

    List<PaymentRecord> query(PaymentRecordQueryParam param);

    PaymentRecord updateStatus(PaymentRecord paymentRecord);
}
