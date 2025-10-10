package com.icht.backfront.dao;

import com.icht.backfront.dataobject.PaymentRecordDO;
import com.icht.backfront.param.PaymentRecordQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaymentRecordDAO {
    int insert(PaymentRecordDO record);
    int update(PaymentRecordDO record);

    List<PaymentRecordDO> select(PaymentRecordQueryParam param);
}
