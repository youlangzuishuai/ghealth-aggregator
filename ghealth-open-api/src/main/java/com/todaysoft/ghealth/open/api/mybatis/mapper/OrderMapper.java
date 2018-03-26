package com.todaysoft.ghealth.open.api.mybatis.mapper;

import java.util.List;

import com.todaysoft.ghealth.open.api.mybatis.model.Order;
import com.todaysoft.ghealth.open.api.mybatis.model.OrderQuery;

public interface OrderMapper
{
    long count(OrderQuery query);
    
    List<Order> query(OrderQuery query);
    
    Order get(String id);
}