package com.todaysoft.ghealth.open.api.service;

import com.hsgene.restful.response.DataResponse;
import com.hsgene.restful.util.CountRecords;
import com.todaysoft.ghealth.open.api.restful.model.OrderDTO;
import com.todaysoft.ghealth.open.api.restful.request.OrderQueryRequest;

public interface IOrderService
{
    DataResponse<CountRecords<OrderDTO>> list(OrderQueryRequest request);
    
    DataResponse<OrderDTO> get(String id);
}
