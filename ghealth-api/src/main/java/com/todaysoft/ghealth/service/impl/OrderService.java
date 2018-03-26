package com.todaysoft.ghealth.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.todaysoft.ghealth.base.request.QueryDetailsRequest;
import com.todaysoft.ghealth.mgmt.request.MaintainOrderRequest;
import com.todaysoft.ghealth.mybatis.model.Agency;
import com.todaysoft.ghealth.mybatis.model.AgencyBill;
import com.todaysoft.ghealth.mybatis.model.AgencyProduct;
import com.todaysoft.ghealth.mybatis.searcher.AgencyProductSearcher;
import com.todaysoft.ghealth.portal.mgmt.facade.AgencyBillFacade;
import com.todaysoft.ghealth.portal.orderEvent.OrderEventLog;
import com.todaysoft.ghealth.service.IAgencyProductService;
import com.todaysoft.ghealth.service.IAgencyService;
import com.todaysoft.ghealth.utils.DataStatus;
import com.todaysoft.ghealth.utils.DateOperateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.todaysoft.ghealth.mybatis.mapper.OrderMapper;
import com.todaysoft.ghealth.mybatis.model.Order;
import com.todaysoft.ghealth.mybatis.searcher.OrderSearcher;
import com.todaysoft.ghealth.service.IOrderService;

@Service
public class OrderService implements IOrderService
{
    @Autowired
    private OrderMapper mapper;
    
    @Autowired
    private IAgencyService agencyService;
    
    @Autowired
    private AgencyBillFacade agencyBillFacade;

    @Autowired
    private IAgencyProductService agencyProductService;

    
    @Override
    public List<Order> list(OrderSearcher searcher)
    {
        return mapper.search(searcher);
    }
    
    @Override
    public List<Order> list(Set<String> codes)
    {
        if (CollectionUtils.isEmpty(codes))
        {
            return Collections.emptyList();
        }
        
        return mapper.getOrdersByCodes(codes);
    }
    
    @Override
    public int count(Object searcher)
    {
        if (!(searcher instanceof OrderSearcher))
        {
            throw new IllegalArgumentException();
        }
        return mapper.count((OrderSearcher)searcher);
    }
    
    @Override
    public List<Order> query(Object searcher, int offset, int limit)
    {
        
        if (!(searcher instanceof OrderSearcher))
        {
            throw new IllegalArgumentException();
        }
        
        if (limit > 0)
        {
            OrderSearcher tis = (OrderSearcher)searcher;
            tis.setLimit(limit);
            tis.setOffset(offset < 0 ? 0 : offset);
        }
        return mapper.search((OrderSearcher)searcher);
    }
    
    @Override
    public Order getOrderById(String id)
    {
        return mapper.get(id);
    }
    
    @Override
    @Transactional
    public void modify(Order data)
    {
        mapper.modify(data);
    }
    
    @Override
    @Transactional
    public void delete(String id)
    {
        mapper.deleteByPrimaryKey(id);
    }
    
    @Override
    @Transactional
    public void create(Order data)
    {
        mapper.insertSelective(data);
    }
    
    @Override
    public List<String> getCodes()
    {
        return mapper.getCodes();
    }
    
    @Override
    public Boolean isUniqueCode(String id, String code)
    {
        OrderSearcher searcher = new OrderSearcher();
        searcher.setOrderCode(code);
        searcher.setOrderCodeExactMatches(true);
        searcher.setId(id);
        return mapper.count(searcher) == 0;
    }
    
    @Override
    public Order getOrderByTaskId(String taskId)
    {
        return mapper.getOrderByTaskId(taskId);
    }
    
    @Override
    @OrderEventLog(eventType = "6", handler = "mgmt")
    public String setOrderStautsSuccessed(QueryDetailsRequest request)
    {
        com.todaysoft.ghealth.mybatis.model.Order data = getOrderByTaskId(request.getId());
        data.setStatus(DataStatus.ORDER_FINISHED);
        mapper.modify(data);
        return data.getId();
    }
    
    @Override
    public Order getByCode(String code)
    {
        return mapper.getByCode(code);
    }
    
    @Override
    @OrderEventLog(eventType = "11", handler = "mgmt")
    public String setOrderStautsDone(QueryDetailsRequest request)
    {
        com.todaysoft.ghealth.mybatis.model.Order data = getOrderByTaskId(request.getId());
        data.setStatus(DataStatus.ORDER_FINISHED);
        mapper.modify(data);
        return data.getId();
    }
    
    @Override
    public String dataDetails(String orderId)
    {
        return mapper.getDataDetails(orderId);
    }
    
    @Override
    public boolean canModify(Order data)
    {
        boolean flag = true;
        String sourceProductId = getOrderById(data.getId()).getProduct().getId();
        if (sourceProductId.equals(data.getProduct().getId()))
        {
            //订单产品没有变动
            flag = false;
        }
        
        if (DataStatus.ORDER_DRAFT.equals(data.getStatus()))
        {
            //订单的状态为草稿
            flag = false;
        }
        return flag;
    }
    
    @Override
    public BigDecimal refundForOrderModify(Agency agency, MaintainOrderRequest request, Order data)
    {
        BigDecimal amountBefore = agency.getAccountAmount();
        BigDecimal orderProductPrice = request.getPrice();
        BigDecimal amountAfter = amountBefore.add(orderProductPrice);
        AgencyBill backAgencyBill = new AgencyBill();
        backAgencyBill.setAgency(agency);
        backAgencyBill.setBillType(DataStatus.BILL_REFUND);
        backAgencyBill.setIncreased(DataStatus.BALANCE_PLUS);
        backAgencyBill.setAmountAfter(amountAfter);
        backAgencyBill.setAmountBefore(amountBefore);
        backAgencyBill.setEventDetails(data.getId());
        backAgencyBill.setTitle("修改订单套餐退还" + orderProductPrice + "元");
        backAgencyBill.setBillTime(new Date());
        agencyBillFacade.create(backAgencyBill);
        agency.setAccountAmount(amountAfter);
        agencyService.modify(agency, null);
        return amountAfter;
    }
    
    @Override
    public void placeForOrderModify(BigDecimal amountAfter, Agency agency, MaintainOrderRequest request, Order data)
    {
//        AgencyProductSearcher searcher = new AgencyProductSearcher();
//        searcher.setProductId(request.getProductId());
//        searcher.setAgencyId(agency.getId());
//        List<AgencyProduct> agencyProducts = agencyProductService.list(searcher);
//        if (!CollectionUtils.isEmpty(agencyProducts))
//        {
//            data.setActualPrice(agencyProducts.get(0).getAgencyPrice());
//        }

        BigDecimal accountAmountAfter = amountAfter.subtract(data.getActualPrice());
        AgencyBill agencyBill = new AgencyBill();
        agencyBill.setAgency(agency);
        agencyBill.setBillType(DataStatus.BILL_PLACE_ORDER);
        agencyBill.setIncreased(DataStatus.BALANCE_REDUCE);
        agencyBill.setAmountAfter(accountAmountAfter);
        agencyBill.setAmountBefore(amountAfter);
        agencyBill.setEventDetails(data.getId());
        agencyBill.setTitle("修改订单套餐扣除" + data.getActualPrice() + "元");
        agencyBillFacade.create(agencyBill, DateOperateUtils.addOneSecond(new Date()));
        agency.setAccountAmount(accountAmountAfter);
        agencyService.modify(agency, null);
    }
}
