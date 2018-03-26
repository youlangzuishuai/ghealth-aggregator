package com.todaysoft.ghealth.portal.mgmt.facade.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.todaysoft.ghealth.base.response.dto.OrderSimpleDTO;
import com.todaysoft.ghealth.mybatis.model.Order;
import com.todaysoft.ghealth.service.wrapper.Wrapper;

@Component
public class OrderSimpleWrapper implements Wrapper<Order, OrderSimpleDTO>
{
    @Override
    public List<OrderSimpleDTO> wrap(List<Order> sources)
    {
        if (CollectionUtils.isEmpty(sources))
        {
            return Collections.emptyList();
        }
        
        OrderSimpleDTO target;
        List<OrderSimpleDTO> targets = new ArrayList<OrderSimpleDTO>();
        
        for (Order source : sources)
        {
            target = new OrderSimpleDTO();
            target.setId(source.getId());
            target.setCode(source.getCode());
            target.setStatus(source.getStatus());
            targets.add(target);
        }
        
        return targets;
    }
}
