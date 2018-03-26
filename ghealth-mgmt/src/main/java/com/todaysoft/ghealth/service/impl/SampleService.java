package com.todaysoft.ghealth.service.impl;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.todaysoft.ghealth.base.response.ObjectResponse;
import com.todaysoft.ghealth.base.response.Pager;
import com.todaysoft.ghealth.base.response.PagerResponse;
import com.todaysoft.ghealth.mgmt.request.MaintainSimpleRequest;
import com.todaysoft.ghealth.mgmt.request.QueryOrdersRequest;
import com.todaysoft.ghealth.model.Order;
import com.todaysoft.ghealth.model.OrderSearcher;
import com.todaysoft.ghealth.model.OrderTest;
import com.todaysoft.ghealth.model.shortMessage.ShortMessage;
import com.todaysoft.ghealth.model.shortMessage.ShortMessageCon;
import com.todaysoft.ghealth.service.IAgencyService;
import com.todaysoft.ghealth.service.IOrderService;
import com.todaysoft.ghealth.service.ISampleService;
import com.todaysoft.ghealth.service.IShortMessageService;
import com.todaysoft.ghealth.service.wrapper.OrderWrapper;
import com.todaysoft.ghealth.support.Pagination;
import com.todaysoft.ghealth.utils.DataStatus;
import com.todaysoft.ghealth.utils.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SampleService implements ISampleService {
    @Autowired
    private Gateway gateway;

    @Autowired
    private OrderWrapper orderWrapper;

    @Autowired
    private IShortMessageService shortMessageService;

    @Autowired
    private IAgencyService agencyService;

    @Autowired
    private IOrderService orderService;



    private final Log logger = LogFactory.getLog(getClass());


    @Override
    public Pagination<Order> search(OrderSearcher searcher, int pageNo, int pageSize)
    {
        QueryOrdersRequest request = orderWrapper.searcherWarp(searcher);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        PagerResponse<com.todaysoft.ghealth.base.response.model.Order> response =
                gateway.request("/mgmt/sample/pager", request, new ParameterizedTypeReference<PagerResponse<com.todaysoft.ghealth.base.response.model.Order>>()
                {
                });
        Pager<com.todaysoft.ghealth.base.response.model.Order> pager = response.getData();

        Pagination<Order> pagination = new Pagination<Order>(pager.getPageNo(), pager.getPageSize(), pager.getTotalCount());

        if (CollectionUtils.isEmpty(pager.getRecords()))
        {
            return pagination;
        }
        pagination.setRecords(orderWrapper.wrap(pager.getRecords()));
        return pagination;
    }


    @Override
    public void modifyStatus(Order data)
    {
        MaintainSimpleRequest request = new MaintainSimpleRequest();
        request.setStatus(DataStatus.ORDER_COLLECTED);
        request.setOrderCodes(data.getOrderIds());
        gateway.request("/mgmt/sample/modifyStatus", request);
    }

    @Override
    public boolean status(Order data)
    {
        MaintainSimpleRequest request = new MaintainSimpleRequest();
        request.setIds(data.getOrderIds());
        ObjectResponse<Boolean> response =
                gateway.request("/mgmt/sample/status", request, new ParameterizedTypeReference<ObjectResponse<Boolean>>()
                {
                });
        return response.getData();
    }

    @Override
    public Order getInformation(String orderCode)
    {
        QueryOrdersRequest request = new QueryOrdersRequest();
        request.setOrderCode(orderCode);
        ObjectResponse<com.todaysoft.ghealth.base.response.model.Order> response = gateway.request("/mgmt/sample/getInformation",
                request,
                new ParameterizedTypeReference<ObjectResponse<com.todaysoft.ghealth.base.response.model.Order>>()
                {
                });
        return orderWrapper.wrap(response.getData());
    }

    @Override
    public void sendMessage(Order data) {
        HashMap<String, Object> result = null;
        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da85c62c9ad015caaaa6f0419eb", "e03254f4da9f4ff0945b65eabddefb56");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da85c62c9ad015caaaa705919f2");// 初始化应用ID
        String split = "-";
        List<OrderTest> list = new ArrayList<OrderTest>();
        for (String code : data.getOrderIds().split(split)){
            if(StringUtils.isNotEmpty(code)){
                Order order = orderService.getByCode(code);
                String phone = order.getCustomer().getPhone();
                try {
                    List<ShortMessage> lists = shortMessageService.getShortMessage(true);
                    List<String> listS = new ArrayList<>();
                    for (ShortMessage shortMessage :lists){
                        listS.add(shortMessage.getAgencyId());
                    }
                    if (listS.contains(order.getAgency().getId())){
                        ShortMessage shortMessage =  shortMessageService.getShortMessage(order.getAgency().getId());

                        ShortMessageCon shortMessageCon = JsonUtils.fromJson(shortMessage.getConfigDetails(),ShortMessageCon.class);
                        if(shortMessageCon.getSampleSigned().getNotifyEnabled().equals("1")){
                            List<String> aa = shortMessageCon.getSampleSigned().getNotifyTarget();
                            for (int a=0;a<aa.size();a++){
                                if(aa.get(a).equals("2")){
                                    result = restAPI.sendTemplateSMS(phone, "231664", new String[] {});
                                }
                            }
                        }
                    }else {
                        List<ShortMessage> shortMessages = shortMessageService.getShortMessage(false);
                        if (shortMessages.size()>0){
                            ShortMessage shortMessage =shortMessages.get(0);
                            ShortMessageCon shortMessageCon = JsonUtils.fromJson(shortMessage.getConfigDetails(),ShortMessageCon.class);
                            if(shortMessageCon.getSampleSigned().getNotifyEnabled().equals("1")){
                                List<String> aa = shortMessageCon.getSampleSigned().getNotifyTarget();
                                for (int a=0;a<aa.size();a++){
                                    if(aa.get(a).equals("2")){
                                        result = restAPI.sendTemplateSMS(phone, "231664", new String[] {});
                                    }
                                }
                            }
                        }
                    }
                }catch (Exception e){
                    logger.error(e);
                }


            }
        }

    }


    @Override
    public void sendMessageToAgency(Order data) {
        HashMap<String, Object> result = null;
        CCPRestSDK restAPI = new CCPRestSDK();
        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
        restAPI.setAccount("8a216da85c62c9ad015caaaa6f0419eb", "e03254f4da9f4ff0945b65eabddefb56");// 初始化主帐号和主帐号TOKEN
        restAPI.setAppId("8a216da85c62c9ad015caaaa705919f2");// 初始化应用ID

        String split = "-";
        List<OrderTest> list = new ArrayList<OrderTest>();
        for (String code : data.getOrderIds().split(split)){
            if(StringUtils.isNotEmpty(code)){
                Order order = orderService.getByCode(code);
                OrderTest orderTest = new OrderTest();
                orderTest.setAgencyId(order.getAgency().getId());
                orderTest.setNumber(1);
                list.add(orderTest);
            }
        }

        List<OrderTest> bi = new ArrayList<OrderTest>();
        for (OrderTest orderTest : list) {
            boolean state = false;
            for (OrderTest orderTests : bi) {
                if(orderTests.getAgencyId().equals(orderTest.getAgencyId())){
                    int number = orderTests.getNumber();
                    number += orderTest.getNumber();
                    orderTests.setNumber(number);
                    state = true;
                }
            }
            if(!state){
                bi.add(orderTest);
            }



        }
        for (OrderTest orderTest : bi) {

            String phone = agencyService.get(orderTest.getAgencyId()).getContactPhone();


            List<ShortMessage> lists = shortMessageService.getShortMessage(true);
            List<String> listS = new ArrayList<>();
            for (ShortMessage shortMessage :lists){
                listS.add(shortMessage.getAgencyId());
            }

            if (listS.contains(orderTest.getAgencyId())){
                ShortMessage shortMessage =  shortMessageService.getShortMessage(orderTest.getAgencyId());
                ShortMessageCon shortMessageCon = JsonUtils.fromJson(shortMessage.getConfigDetails(),ShortMessageCon.class);
                if(shortMessageCon.getSampleSigned().getNotifyEnabled().equals("1")){
                    List<String> aa = shortMessageCon.getSampleSigned().getNotifyTarget();
                    for (int a=0;a<aa.size();a++){
                        if (aa.get(a).equals("1")){
                            result = restAPI.sendTemplateSMS(phone, "231669", new String[] {orderTest.getNumber().toString()});
                        }
                    }
                }
            }else {
                List<ShortMessage> shortMessages = shortMessageService.getShortMessage(false);
                if (shortMessages.size()>0){
                    ShortMessage shortMessage=shortMessages.get(0);
                    ShortMessageCon shortMessageCon = JsonUtils.fromJson(shortMessage.getConfigDetails(),ShortMessageCon.class);
                    if(shortMessageCon.getSampleSigned().getNotifyEnabled().equals("1")){
                        List<String> aa = shortMessageCon.getSampleSigned().getNotifyTarget();
                        for (int a=0;a<aa.size();a++){
                            if (aa.get(a).equals("1")){
                                result = restAPI.sendTemplateSMS(phone, "231669", new String[] {orderTest.getNumber().toString()});
                            }
                        }
                    }
                }
            }

        }


    }
}
