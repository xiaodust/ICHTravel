package com.icht.backfront.dao;

import com.icht.backfront.dataobject.OrderDO;
import com.icht.backfront.param.QueryOrderParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDAO {
    int insert(OrderDO orderDO);

    int update(OrderDO orderDO);

    OrderDO selectByOrderNumber(String orderNumber);

    int selectCounts(QueryOrderParam param);

    List<OrderDO> pageQuery(QueryOrderParam param);


}
