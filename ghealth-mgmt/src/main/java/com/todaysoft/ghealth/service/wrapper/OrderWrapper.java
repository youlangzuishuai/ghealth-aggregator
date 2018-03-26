package com.todaysoft.ghealth.service.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.todaysoft.ghealth.mgmt.request.QueryOrderHistoryRequest;
import com.todaysoft.ghealth.model.statistics.StatisticsSearcher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.todaysoft.ghealth.mgmt.request.QueryOrdersRequest;
import com.todaysoft.ghealth.model.Order;
import com.todaysoft.ghealth.model.OrderHistory;
import com.todaysoft.ghealth.model.OrderSearcher;
import com.todaysoft.ghealth.model.agency.Agency;
import com.todaysoft.ghealth.model.customer.Customer;

@Component
public class OrderWrapper
{
    @Autowired
    private ProductWrapper productWrapper;
    
    public List<Order> wrap(List<com.todaysoft.ghealth.base.response.model.Order> records)
    {
        if (CollectionUtils.isEmpty(records))
        {
            return Collections.emptyList();
        }
        Order order;
        List<Order> orders = new ArrayList<Order>();
        for (com.todaysoft.ghealth.base.response.model.Order record : records)
        {
            order = new Order();
            wrapRecord(record, order);
            orders.add(order);
        }
        return orders;
    }
    
    public Order wrap(com.todaysoft.ghealth.base.response.model.Order record)
    {
        if (null == record)
        {
            return null;
        }
        
        Order order = new Order();
        wrapRecord(record, order);
        return order;
    }
    
    private void wrapRecord(com.todaysoft.ghealth.base.response.model.Order source, Order target)
    {
        if (null != source.getProduct())
        {
            target.setProduct(productWrapper.wrap(source.getProduct()));
        }
        
        if (null != source.getCustomer())
        {
            Customer data = new Customer();
            BeanUtils.copyProperties(source.getCustomer(), data, "createTime", "updateTime", "deleteTime");
            target.setCustomer(data);
        }
        
        if (null != source.getAgency())
        {
            Agency data = new Agency();
            BeanUtils.copyProperties(source.getAgency(), data, "createTime", "updateTime", "deleteTime");
            target.setAgency(data);
        }
        
        BeanUtils.copyProperties(source, target, "createTime", "updateTime", "deleteTime", "submitTime");
        target.setCreateTime(null == source.getCreateTime() ? null : new Date(source.getCreateTime()));
        target.setSubmitTime(null == source.getSubmitTime() ? null : new Date(source.getSubmitTime()));
    }
    
    public QueryOrdersRequest searcherWarp(OrderSearcher searcher)
    {
        QueryOrdersRequest request = new QueryOrdersRequest();
        BeanUtils.copyProperties(searcher, request, "startTime", "endTime");
        if (null != searcher.getStartTime())
        {
            request.setStartCreateTime(String.valueOf(searcher.getStartTime().getTime()));
        }
        if (null != searcher.getEndTime())
        {
            request.setEndStartTime(String.valueOf(searcher.getEndTime().getTime()));
        }
        return request;
    }
    
    public List<OrderHistory> wrapOrderHistory(List<com.todaysoft.ghealth.base.response.model.OrderHistory> records)
    {
        if (CollectionUtils.isEmpty(records))
        {
            return Collections.emptyList();
        }
        OrderHistory OrderHistory;
        List<OrderHistory> OrderHistorys = new ArrayList<OrderHistory>();
        for (com.todaysoft.ghealth.base.response.model.OrderHistory record : records)
        {
            OrderHistory = new OrderHistory();
            wrapOrderHistory(record, OrderHistory);
            OrderHistorys.add(OrderHistory);
        }
        return OrderHistorys;
    }
    
    public OrderHistory wrapOrderHistory(com.todaysoft.ghealth.base.response.model.OrderHistory record)
    {
        if (null == record)
        {
            return null;
        }
        
        OrderHistory OrderHistory = new OrderHistory();
        wrapOrderHistory(record, OrderHistory);
        return OrderHistory;
    }
    
    private void wrapOrderHistory(com.todaysoft.ghealth.base.response.model.OrderHistory source, OrderHistory target)
    {
        BeanUtils.copyProperties(source, target, "eventTime");
        target.setEventTime(null == source.getEventTime() ? null : new Date(source.getEventTime()));
    }


    public QueryOrderHistoryRequest searcherOrderHistoryWarp(StatisticsSearcher searcher)
    {
        QueryOrderHistoryRequest request = new QueryOrderHistoryRequest();
        BeanUtils.copyProperties(searcher, request, "startTime", "endTime");
        if (null != searcher.getStartTime())
        {
            request.setStartTime(String.valueOf(searcher.getStartTime().getTime()));
        }
        if (null != searcher.getEndTime())
        {
            request.setEndTime(String.valueOf(searcher.getEndTime().getTime()));
        }
        return request;
    }
}
