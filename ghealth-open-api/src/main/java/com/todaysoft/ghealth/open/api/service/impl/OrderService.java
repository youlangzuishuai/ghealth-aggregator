package com.todaysoft.ghealth.open.api.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hsgene.restful.response.DataResponse;
import com.hsgene.restful.util.CountRecords;
import com.todaysoft.ghealth.open.api.mybatis.mapper.OrderMapper;
import com.todaysoft.ghealth.open.api.mybatis.model.Order;
import com.todaysoft.ghealth.open.api.mybatis.model.OrderQuery;
import com.todaysoft.ghealth.open.api.restful.model.OrderDTO;
import com.todaysoft.ghealth.open.api.restful.request.OrderQueryRequest;
import com.todaysoft.ghealth.open.api.service.IOrderService;
import com.todaysoft.ghealth.open.api.service.parser.OrderQueryParser;
import com.todaysoft.ghealth.open.api.service.wrapper.OrderWrapper;

@Service
public class OrderService implements IOrderService
{
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderWrapper orderWrapper;
    
    @Autowired
    private OrderQueryParser orderQueryParser;
    
    @Override
    public DataResponse<CountRecords<OrderDTO>> list(OrderQueryRequest request)
    {
        OrderQuery query = orderQueryParser.parse(request);
        
        CountRecords<OrderDTO> data = new CountRecords<OrderDTO>();
        
        if (request.isCount())
        {
            long count = orderMapper.count(query);
            data.setCount(count);
            
            if (0 == count)
            {
                data.setRecords(Collections.emptyList());
                return new DataResponse<CountRecords<OrderDTO>>(data);
            }
            
            if (null != request.getLimit() && null != request.getOffset() && request.getOffset().intValue() >= count)
            {
                int offset;
                int limit = request.getLimit().intValue();
                int mod = (int)count % limit;
                
                if (0 == mod)
                {
                    offset = (((int)count / limit) - 1) * limit;
                }
                else
                {
                    offset = ((int)count / limit) * limit;
                }
                
                query.setOffset(offset);
            }
        }
        
        List<Order> records = orderMapper.query(query);
        data.setRecords(orderWrapper.wrap(records));
        return new DataResponse<CountRecords<OrderDTO>>(data);
    }
    
    @Override
    public DataResponse<OrderDTO> get(String id)
    {
        Order order = orderMapper.get(id);
        return new DataResponse<OrderDTO>(orderWrapper.wrap(order));
    }
}
