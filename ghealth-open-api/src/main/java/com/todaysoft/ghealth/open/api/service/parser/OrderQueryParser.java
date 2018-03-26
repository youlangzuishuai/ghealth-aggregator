package com.todaysoft.ghealth.open.api.service.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hsgene.restful.request.Orderby;
import com.todaysoft.ghealth.open.api.mybatis.model.OrderQuery;
import com.todaysoft.ghealth.open.api.mybatis.model.OrderbyClause;
import com.todaysoft.ghealth.open.api.restful.request.OrderQueryRequest;

@Component
public class OrderQueryParser
{
    public OrderQuery parse(OrderQueryRequest request)
    {
        if (null == request)
        {
            throw new IllegalArgumentException();
        }
        
        OrderQuery query = new OrderQuery();
        BeanUtils.copyProperties(request, query, "orderbys");
        
        if (!CollectionUtils.isEmpty(request.getOrderbys()))
        {
            OrderbyClause orderby;
            List<OrderbyClause> orderbys = new ArrayList<OrderbyClause>();
            
            for (Orderby ob : request.getOrderbys())
            {
                orderby = new OrderbyClause();
                orderby.setField(ob.getField());
                orderby.setAsc(null == ob.getAsc() || ob.getAsc());
                orderbys.add(orderby);
            }
            
            query.setOrderbys(orderbys);
        }
        
        return query;
    }
}
