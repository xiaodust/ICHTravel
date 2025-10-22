package com.icht.backfront.service.impl;

import com.icht.backfront.dao.PaymentRecordDAO;
import com.icht.backfront.dataobject.PaymentRecordDO;
import com.icht.backfront.model.PaymentRecord;
import com.icht.backfront.param.PaymentRecordQueryParam;
import com.icht.backfront.service.PaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {
    @Autowired
    private PaymentRecordDAO paymentRecordDAO;

    @Override
    public PaymentRecord save(PaymentRecord paymentRecord) {
        if (paymentRecord==null){
            return null;
        }

        if (StringUtils.isEmpty(paymentRecord.getId())){
            PaymentRecordDO paymentRecordDO=new PaymentRecordDO(paymentRecord);
            paymentRecordDO.setId(UUID.randomUUID().toString());
            paymentRecordDO.setGmtCreated(LocalDateTime.now());
            paymentRecordDO.setGmtModified(LocalDateTime.now());
            int insert = paymentRecordDAO.insert(paymentRecordDO);
            if (insert<1){
                return null;
            }
            paymentRecord.setId(paymentRecordDO.getId());
            return paymentRecord;
        }else {
            PaymentRecordDO paymentRecordDO = new PaymentRecordDO(paymentRecord);
            paymentRecordDO.setGmtModified(LocalDateTime.now());
            int updateSize = paymentRecordDAO.update(paymentRecordDO);
            if (updateSize < 1) {
                return null;
            }
            return paymentRecord;
        }

    }

    @Override
    public List<PaymentRecord> query(PaymentRecordQueryParam param) {
       List<PaymentRecord> records=new ArrayList<PaymentRecord>();
       List<PaymentRecordDO> recordDOS=paymentRecordDAO.select(param);
       if (CollectionUtils.isEmpty(recordDOS)){
           return records;
       }
       for (PaymentRecordDO paymentRecordDO : recordDOS) {
           PaymentRecord paymentRecord=paymentRecordDO.TOModel();
           records.add(paymentRecord);
       }
       return records;
    }

    @Override
    public PaymentRecord updateStatus(PaymentRecord paymentRecord) {
        if (paymentRecord==null){
            return null;
        }
        PaymentRecordDO paymentRecordDO=new PaymentRecordDO(paymentRecord);
        paymentRecordDO.setGmtModified(LocalDateTime.now());
        int updateSize = paymentRecordDAO.update(paymentRecordDO);
        if (updateSize < 1) {
            return null;
        }

        return paymentRecord;
    }
}
