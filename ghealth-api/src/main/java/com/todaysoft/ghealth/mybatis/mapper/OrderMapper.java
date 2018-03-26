package com.todaysoft.ghealth.mybatis.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import com.todaysoft.ghealth.mybatis.model.Order;
import com.todaysoft.ghealth.mybatis.searcher.OrderSearcher;

public interface OrderMapper
{
    List<Order> search(OrderSearcher searcher);
    
    int count(OrderSearcher searcher);
    
    List<Order> getOrdersByCodes(@Param("codes") Set<String> codes);
    
    int deleteByPrimaryKey(String id);
    
    int insert(Order record);
    
    int insertSelective(Order record);
    
    Order get(String id);
    
    int modify(Order record);
    
    int modifyState(Order record);
    
    List<String> getCodes();

    Order getOrderByTaskId(String taskId);

    Order getByCode(String code);

    String getDataDetails(String orderId);
}