package com.todaysoft.ghealth.portal.orderEvent;

import java.util.Date;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.todaysoft.ghealth.mybatis.model.OrderHistory;
import com.todaysoft.ghealth.service.IOrderHistoryService;
import com.todaysoft.ghealth.utils.IdGen;

@Aspect
@Component
public class SetOrderStatustoSavePlacedCancel extends OrderEventAspectJ
{
    
    @Autowired
    private IOrderHistoryService orderHistoryService;
    
    @Override
    public void setOrderStatusByMethodReturnString(JoinPoint point, Map<String, String> map, Object returnObj)
    {
        OrderHistory orderHistory = new OrderHistory();
        String orderId = null;
        String operatorName = getOperatorName(map, point);
        if (returnObj instanceof String)
        {
            orderId = (String)returnObj;
        }
        orderHistory.setId(IdGen.uuid());
        orderHistory.setOrderId(orderId);
        orderHistory.setEventTime(new Date());
        orderHistory.setOperatorName(operatorName);
        setOrderEventStatus(map.get(POINT_EVENTTYPE), orderHistory);
        orderHistoryService.create(orderHistory);
    }
}
