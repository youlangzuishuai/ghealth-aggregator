package com.todaysoft.ghealth.mybatis.mapper;

import com.todaysoft.ghealth.mybatis.model.SampleDeliveryDetails;

public interface SampleDeliveryDetailsMapper
{
    int create(SampleDeliveryDetails record);
    
    SampleDeliveryDetails get(String id);
    
    SampleDeliveryDetails getByOrderId(String orderId);
    
    int modify(SampleDeliveryDetails record);
    
}