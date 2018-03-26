package com.todaysoft.ghealth.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.todaysoft.ghealth.base.request.QueryDetailsRequest;
import com.todaysoft.ghealth.mgmt.request.MaintainOrderRequest;
import com.todaysoft.ghealth.mybatis.model.Agency;
import com.todaysoft.ghealth.mybatis.model.Order;
import com.todaysoft.ghealth.mybatis.searcher.OrderSearcher;
import com.todaysoft.ghealth.service.impl.PagerQueryHandler;

public interface IOrderService extends PagerQueryHandler<Order>
{
    List<Order> list(OrderSearcher searcher);
    
    List<Order> list(Set<String> codes);
    
    Order getOrderById(String id);
    
    void modify(Order data);
    
    void delete(String id);
    
    void create(Order data);
    
    List<String> getCodes();
    
    Boolean isUniqueCode(String id, String code);
    
    Order getOrderByTaskId(String taskId);
    
    String setOrderStautsSuccessed(QueryDetailsRequest request);
    
    Order getByCode(String code);
    
    String setOrderStautsDone(QueryDetailsRequest request);
    
    String dataDetails(String orderId);
    
    boolean canModify(Order data);
    
    BigDecimal refundForOrderModify(Agency agency, MaintainOrderRequest request, Order data);
    
    void placeForOrderModify(BigDecimal amountAfter, Agency agency, MaintainOrderRequest request, Order data);
}
