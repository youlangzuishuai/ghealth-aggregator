package com.todaysoft.ghealth.portal.mgmt.facade;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Bidi;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.todaysoft.ghealth.mybatis.model.*;
import com.todaysoft.ghealth.mybatis.searcher.ItemLocusSearcher;
import com.todaysoft.ghealth.portal.mgmt.facade.report.algorithm.AbstractTestingItemAlgorithm;
import com.todaysoft.ghealth.portal.mgmt.facade.report.algorithm.TestingItemAlgorithmFactory;
import com.todaysoft.ghealth.service.*;
import com.todaysoft.ghealth.service.impl.core.TestingItemAlgorithmConfig;
import com.todaysoft.ghealth.utils.DateOperateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.todaysoft.ghealth.base.request.QueryDetailsRequest;
import com.todaysoft.ghealth.base.response.ListResponse;
import com.todaysoft.ghealth.base.response.ObjectResponse;
import com.todaysoft.ghealth.base.response.Pager;
import com.todaysoft.ghealth.base.response.PagerResponse;
import com.todaysoft.ghealth.base.response.dto.OrderSimpleDTO;
import com.todaysoft.ghealth.base.response.model.Order;
import com.todaysoft.ghealth.base.response.model.OrderHistory;
import com.todaysoft.ghealth.mgmt.model.OrderReportStreamDTO;
import com.todaysoft.ghealth.mgmt.model.ReportGenerateTaskDTO;
import com.todaysoft.ghealth.mgmt.request.DownloadOrderReportRequest;
import com.todaysoft.ghealth.mgmt.request.MaintainOrderHistoryRequest;
import com.todaysoft.ghealth.mgmt.request.MaintainOrderRequest;
import com.todaysoft.ghealth.mgmt.request.QueryOrderByCodesRequest;
import com.todaysoft.ghealth.mgmt.request.QueryOrderHistoryRequest;
import com.todaysoft.ghealth.mgmt.request.QueryOrdersRequest;
import com.todaysoft.ghealth.mybatis.searcher.AgencyProductSearcher;
import com.todaysoft.ghealth.mybatis.searcher.OrderSearcher;
import com.todaysoft.ghealth.mybatis.searcher.StatisticsSearcher;
import com.todaysoft.ghealth.portal.mgmt.MgmtErrorCode;
import com.todaysoft.ghealth.portal.mgmt.facade.report.ReportGenerateContext;
import com.todaysoft.ghealth.portal.mgmt.facade.wrapper.OrderSimpleWrapper;
import com.todaysoft.ghealth.portal.mgmt.facade.wrapper.ReportGenerateTaskWrapper;
import com.todaysoft.ghealth.service.impl.PagerQueryer;
import com.todaysoft.ghealth.service.impl.ServiceException;
import com.todaysoft.ghealth.service.impl.core.TestingItemEvaluateConfig;
import com.todaysoft.ghealth.service.model.AliyunStorageObject;
import com.todaysoft.ghealth.service.model.LocalStorageObject;
import com.todaysoft.ghealth.service.model.StorageObject;
import com.todaysoft.ghealth.service.wrapper.OrderWrapper;
import com.todaysoft.ghealth.utils.DataStatus;
import com.todaysoft.ghealth.utils.JsonUtils;

@Component
public class OrderMgmtFacade
{
    private static Logger log = LoggerFactory.getLogger(OrderMgmtFacade.class);
    
    @Autowired
    private IAccountService accountService;
    
    @Autowired
    private IOrderService service;
    
    @Autowired
    private OrderWrapper wrapper;
    
    @Autowired
    private ReportGenerateTaskWrapper reportGenerateTaskWrapper;
    
    @Autowired
    private OrderSimpleWrapper orderSimpleWrapper;
    
    @Autowired
    private IReportTemplateService reportTemplateService;
    
    @Autowired
    private ICustomerService customerService;
    
    @Autowired
    private ITestingDataService testingDataService;
    
    @Autowired
    private ITestingItemService testingItemService;
    
    @Autowired
    private ITestingProductService testingProductService;
    
    @Autowired
    private IReportGenerateService reportGenerateService;
    
    @Autowired
    private IOrderHistoryService orderHistoryService;
    
    @Autowired
    private IAgencyService agencyService;
    
    @Autowired
    private AgencyBillFacade agencyBillFacade;

    @Autowired
    private IAgencyProductService agencyProductService;
    
    @Autowired
    private IObjectStorageService objectStorageService;
    
    @Autowired
    private IShortMessageService shortMessageService;
    
    @Autowired
    private IItemLocusService testingItemLocusService;

    @Autowired
    private ICancerService cancerService;
    
    public PagerResponse<Order> pager(QueryOrdersRequest request)
    {
        int pageNo = null == request.getPageNo() ? 1 : request.getPageNo();
        int pageSize = null == request.getPageSize() ? 10 : request.getPageSize();
        OrderSearcher searcher = new OrderSearcher();
        BeanUtils.copyProperties(request, searcher, "startCreateTime", "endStartTime");
        wrapSearcher(request, searcher);
        PagerQueryer<com.todaysoft.ghealth.mybatis.model.Order> queryer = new PagerQueryer<com.todaysoft.ghealth.mybatis.model.Order>(service);
        Pager<com.todaysoft.ghealth.mybatis.model.Order> pager = queryer.query(searcher, pageNo, pageSize);
        Pager<Order> result = Pager.generate(pager.getPageNo(), pager.getPageSize(), pager.getTotalCount(), wrapper.wrap(pager.getRecords()));
        return new PagerResponse<Order>(result);
    }
    
    public ObjectResponse<Order> display(MaintainOrderRequest request)
    {
        com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderById(request.getId());
        return new ObjectResponse<Order>(wrapper.wrap(order));
    }
    
    public ListResponse<Order> list(QueryOrdersRequest request)
    {
        OrderSearcher searcher = new OrderSearcher();
        BeanUtils.copyProperties(request, searcher, "startCreateTime", "endStartTime");
        wrapSearcher(request, searcher);
        return new ListResponse<Order>(wrapper.wrap(service.list(searcher)));
    }
    
    public ListResponse<OrderSimpleDTO> list(QueryOrderByCodesRequest request)
    {
        List<com.todaysoft.ghealth.mybatis.model.Order> orders = service.list(request.getCodes());
        return new ListResponse<OrderSimpleDTO>(orderSimpleWrapper.wrap(orders));
    }
    
    private String transferLongToDate(String dateFormat, Long millSec)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }
    
    private void wrapSearcher(QueryOrdersRequest request, OrderSearcher searcher)
    {
        if (StringUtils.isNotEmpty(request.getEndStartTime()))
        {
            searcher.setEndStartTime(transferLongToDate("yyyy-MM-dd 23:59:59", Long.valueOf(request.getEndStartTime())));
        }
        if (StringUtils.isNotEmpty(request.getStartCreateTime()))
        {
            searcher.setStartCreateTime(transferLongToDate("yyyy-MM-dd 00:00:00", Long.valueOf(request.getStartCreateTime())));
        }
    }
    
    public ListResponse<Order> getOrdersByIds(MaintainOrderRequest request)
    {
        List<Order> orders = new ArrayList<Order>();
        if (StringUtils.isNotEmpty(request.getId()))
        {
            String split = "-";
            for (String id : request.getId().split(split))
            {
                com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderById(id);
                orders.add(wrapper.wrap(order));
            }
            return new ListResponse<>(orders);
        }
        return new ListResponse<>(Collections.emptyList());
    }
    
    public ObjectResponse<String> generateReport(MaintainOrderRequest request)
    {
        ReportGenerateContext context = getReportGenerateContext(request);
        String taskId = reportGenerateService.generate(context);
        return new ObjectResponse<String>(taskId);
    }
    
    public ListResponse<String> generateReports(MaintainOrderRequest request)
    {
        List<String> taskIds = new ArrayList<>();
        String[] orderIds = request.getId().split("-");
        for (String orderId : orderIds)
        {
            if (StringUtils.isNotEmpty(orderId))
            {
                request.setId(orderId);
                ReportGenerateContext context = getReportGenerateContext(request);
                taskIds.add(reportGenerateService.generate(context));
            }
        }
        return new ListResponse<String>(taskIds);
    }
    
    public ObjectResponse<String> regenerateReport(MaintainOrderRequest request)
    {
        ReportGenerateContext context = getReportGenerateContext(request);
        String taskId = reportGenerateService.generate(context);
        return new ObjectResponse<String>(taskId);
    }
    
    public ObjectResponse<ReportGenerateTaskDTO> getReportGenerateTask(QueryDetailsRequest request)
    {
        if (StringUtils.isEmpty(request.getId()))
        {
            throw new ServiceException(MgmtErrorCode.API_ILLEGAL_ARGUMENT);
        }
        
        ReportGenerateTask entity = reportGenerateService.getReportGenerateTask(request.getId());
        if (ReportGenerateTask.STATUS_SUCCESS.equals(entity.getStatus()))
        {
            com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderByTaskId(request.getId());
            if (DataStatus.ORDER_UPLOADED.equals(order.getStatus()))
            {
                service.setOrderStautsSuccessed(request);
                //   service.setOrderStautsDone(request);
            }
        }
        return new ObjectResponse<ReportGenerateTaskDTO>(reportGenerateTaskWrapper.wrap(entity));
    }
    
    public ListResponse<ReportGenerateTaskDTO> getReportGenerateTasks(QueryDetailsRequest request)
    {
        if (StringUtils.isEmpty(request.getId()))
        {
            throw new ServiceException(MgmtErrorCode.API_ILLEGAL_ARGUMENT);
        }
        List<ReportGenerateTaskDTO> list = new ArrayList<>();
        String[] taskIds = request.getId().split("-");
        for (String taskId : taskIds)
        {
            if (StringUtils.isNotEmpty(taskId))
            {
                request.setId(taskId);
                com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderByTaskId(request.getId());
                ReportGenerateTask entity = reportGenerateService.getReportGenerateTask(request.getId());
                boolean flag = false;
                if (order.getStatus().equals("4"))
                {
                    flag = true;
                }
                
                if (ReportGenerateTask.STATUS_SUCCESS.equals(entity.getStatus()))
                {
                    service.setOrderStautsSuccessed(request);
                    //    service.setOrderStautsDone(request);
                    if (flag)
                    {
                        HashMap<String, Object> result = null;
                        CCPRestSDK restAPI = new CCPRestSDK();
                        restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
                        restAPI.setAccount("8a216da85c62c9ad015caaaa6f0419eb", "e03254f4da9f4ff0945b65eabddefb56");// 初始化主帐号和主帐号TOKEN
                        restAPI.setAppId("8a216da85c62c9ad015caaaa705919f2");// 初始化应用ID
                        String phone = order.getCustomer().getPhone();
                        try
                        {
                            List<ShortMessage> lists = shortMessageService.getShortMessage(true);
                            List<String> listS = new ArrayList<>();
                            for (ShortMessage shortMessage : lists)
                            {
                                listS.add(shortMessage.getAgencyId());
                            }
                            if (listS.contains(order.getAgency().getId()))
                            {
                                ShortMessage shortMessage = shortMessageService.getByAgencyId(order.getAgency().getId());
                                ShortMessageCon shortMessageCon = JsonUtils.fromJson(shortMessage.getConfigDetails(), ShortMessageCon.class);
                                if (shortMessageCon.getReportGenerated().getNotifyEnabled().equals("1"))
                                {
                                    List<String> aa = shortMessageCon.getReportGenerated().getNotifyTarget();
                                    for (int a = 0; a < aa.size(); a++)
                                    {
                                        if (aa.get(a).equals("2"))
                                        {
                                            result = restAPI.sendTemplateSMS(phone, "231668", new String[] {});
                                        }
                                    }
                                }
                            }
                            else
                            {
                                List<ShortMessage> shortMessages = shortMessageService.getShortMessage(false);
                                if (shortMessages.size() > 0)
                                {
                                    ShortMessage shortMessage = shortMessages.get(0);
                                    ShortMessageCon shortMessageCon = JsonUtils.fromJson(shortMessage.getConfigDetails(), ShortMessageCon.class);
                                    if (shortMessageCon.getReportGenerated().getNotifyEnabled().equals("1"))
                                    {
                                        List<String> aa = shortMessageCon.getReportGenerated().getNotifyTarget();
                                        for (int a = 0; a < aa.size(); a++)
                                        {
                                            if (aa.get(a).equals("2"))
                                            {
                                                result = restAPI.sendTemplateSMS(phone, "231668", new String[] {});
                                            }
                                        }
                                    }
                                }
                            }
                            
                        }
                        catch (Exception e)
                        {
                            
                        }
                    }
                    
                }
                list.add(reportGenerateTaskWrapper.wrap(entity));
            }
            
        }
        return new ListResponse<ReportGenerateTaskDTO>(list);
    }
    
    public ObjectResponse<OrderReportStreamDTO> getReportStream(DownloadOrderReportRequest request)
    {
        if (StringUtils.isEmpty(request.getOrderId()))
        {
            throw new ServiceException(MgmtErrorCode.API_ILLEGAL_ARGUMENT);
        }
        
        com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderById(request.getOrderId());
        
        if (StringUtils.isEmpty(order.getReportGenerateTaskId()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_DOWNLOAD_UNGENERATED);
        }
        
        ReportGenerateTask task = reportGenerateService.getReportGenerateTask(order.getReportGenerateTaskId());
        
        if (null == task || !ReportGenerateTask.STATUS_SUCCESS.equals(task.getStatus()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_DOWNLOAD_UNGENERATED);
        }
        
        String objectStorageKey;
        
        if ("word".equals(request.getType()))
        {
            objectStorageKey = task.getWordFileUrl();
        }
        else
        {
            objectStorageKey = task.getPdfFileUrl();
        }
        
        if (StringUtils.isEmpty(objectStorageKey))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_DOWNLOAD_UNGENERATED);
        }
        
        ObjectStorage objectStorage = objectStorageService.get(objectStorageKey);
        StorageObject object = getStorageObject(objectStorage);
        
        try
        {
            String suffix = object.getSuffix();
            byte[] bytes = object.getObjectContent();
            OrderReportStreamDTO stream = new OrderReportStreamDTO();
            stream.setSuffix(suffix);
            stream.setContent(bytes);
            return new ObjectResponse<OrderReportStreamDTO>(stream);
        }
        catch (IOException e)
        {
            throw new ServiceException(MgmtErrorCode.REPORT_DOWNLOAD_IO_ERROR);
        }
    }
    
    private StorageObject getStorageObject(ObjectStorage entity)
    {
        if (null == entity)
        {
            return null;
        }
        
        Map<String, String> attributes = JsonUtils.fromJson(entity.getStorageDetails(), new TypeReference<Map<String, String>>()
        {
        });
        
        if (ObjectStorage.STORAGE_ALI_OSS.equals(entity.getStorageType()))
        {
            AliyunStorageObject object = new AliyunStorageObject(attributes.get("endpoint"), attributes.get("bucketName"), attributes.get("objectKey"));
            return object;
        }
        else if (ObjectStorage.STORAGE_LOCAL.equals(entity.getStorageType()))
        {
            String uri = attributes.get("uri");
            String path = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest().getServletContext().getRealPath(uri);
            LocalStorageObject object = new LocalStorageObject(uri, path);
            return object;
        }
        else
        {
            throw new IllegalStateException();
        }
    }
    
    private ReportGenerateContext getReportGenerateContext(MaintainOrderRequest request)
    {
        String id = request.getId();
        
        if (StringUtils.isEmpty(id))
        {
            throw new ServiceException(MgmtErrorCode.API_ILLEGAL_ARGUMENT);
        }
        
        ManagementAccountDetails account = accountService.getManagementAccountDetails(request.getToken());
        
        if (null == account)
        {
            throw new IllegalStateException();
        }
        
        com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderById(id);
        
        if (null == order)
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_ORDER_NOT_EXISTS);
        }
        
        List<LocusGenetype> genetypes = testingDataService.getOrderTestingData(order.getId());
        
        if (CollectionUtils.isEmpty(genetypes))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_TESTING_DATA_NOT_EXISTS);
        }
        
        Customer customer = order.getCustomer();
        
        if (null == customer || StringUtils.isEmpty(customer.getId()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_CUSTOMER_NOT_EXISTS);
        }
        
        customer = customerService.get(customer.getId());
        
        if (null == customer)
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_CUSTOMER_NOT_EXISTS);
        }
        
        Product product = order.getProduct();
        
        if (null == product || StringUtils.isEmpty(product.getId()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_PRODUCT_NOT_EXISTS);
        }
        
        product = testingProductService.get(product.getId());
        
        if (null == product)
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_PRODUCT_NOT_EXISTS);
        }
        
        ReportTemplate template = reportTemplateService.getReportTemplate(product.getId(), null == order.getAgency() ? null : order.getAgency().getId());
        
        if (null == template || StringUtils.isEmpty(template.getTsdgKey()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_TEMPLATE_NOT_EXISTS);
        }
        
        List<com.todaysoft.ghealth.mybatis.model.TestingItem> testingItems = testingItemService.getItemsForProduct(product.getId());
        
        if (CollectionUtils.isEmpty(testingItems))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_TESING_ITEMS_NOT_EXISTS);
        }
        
        if (StringUtils.isEmpty(customer.getSex()) && testingItemService.isRequiredSexForGenerate(testingItems))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_GENERATE_CUSTOMER_SEX_UNDEFINED);
        }
        
        TestingItemAlgorithmConfig algorithmConfig;
        List<TestingItemAlgorithmConfig> testingItemAlgorithmConfigs = new ArrayList<>();
        for (com.todaysoft.ghealth.mybatis.model.TestingItem testingItem : testingItems)
        {
            AbstractTestingItemAlgorithm algorithm = TestingItemAlgorithmFactory.getAlgorithm(testingItem);
            
            algorithmConfig = algorithm.getTestingItemAlgorithmConfig(testingItem);
            ItemLocusSearcher searcher = new ItemLocusSearcher();
            searcher.setItemId(testingItem.getId());
            List<TestingItemLocus> records = testingItemLocusService.list(searcher);
            algorithmConfig.setAlgorithm(algorithm);
            algorithmConfig.setTestingItem(testingItem);
            algorithmConfig.setLocusConfig(algorithm.getLocusConfig(testingItem.getName(), records));
            
            testingItemAlgorithmConfigs.add(algorithmConfig);
        }
        
        Map<String, String> genetypeMappings = new HashMap<String, String>();
        genetypes.forEach(genetype -> genetypeMappings.put(genetype.getLocus(), genetype.getGenetype()));
        
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        String destUri = "/report/" + order.getCode();
        String destDirectory = ((ServletRequestAttributes)attributes).getRequest().getServletContext().getRealPath(destUri);
        
        ReportGenerateContext context = new ReportGenerateContext();
        context.setOrder(order);
        context.setProduct(product);
        context.setCustomer(customer);
        context.setGenetypes(genetypeMappings);
        //  context.setTestingItemEvaluateConfigs(testingItemEvaluateConfigs);
        context.setTestingItemAlgorithmConfigs(testingItemAlgorithmConfigs);
        context.setOperator(account);
        context.setTemplateKey(template.getTsdgKey());
        context.setDestUri(destUri);
        context.setDestDirectory(destDirectory);
        return context;
    }
    
    public ListResponse<OrderHistory> getOrderHistoriesByOrderId(MaintainOrderRequest request)
    {
        List<com.todaysoft.ghealth.mybatis.model.OrderHistory> list = orderHistoryService.getOrderHistoriesByOrderId(request.getId());
        return new ListResponse<>(wrapper.wrapOrderHistory(list));
    }
    
    @Transactional
    public String modify(MaintainOrderRequest request)
    {
        ManagementAccountDetails account = accountService.getManagementAccountDetails(request.getToken());
        
        if (null == account)
        {
            throw new IllegalStateException();
        }
        Agency agency = agencyService.get(request.getAgencyId());
        com.todaysoft.ghealth.mybatis.model.Order data = wrapOrder(request);
        if (service.canModify(data))
        {
            BigDecimal amountAfter = service.refundForOrderModify(agency, request, data);
            service.placeForOrderModify(amountAfter, agency, request, data);
        }
        data.setUpdateTime(new Date());
        data.setUpdatorName(account.getName());
        service.modify(data);
        return data.getId();
    }

    
    public ObjectResponse<Boolean> isUniqueCode(MaintainOrderRequest request)
    {
        Boolean isUniqueCode = service.isUniqueCode(request.getId(), request.getCode());
        return new ObjectResponse<>(isUniqueCode);
    }
    
    private com.todaysoft.ghealth.mybatis.model.Order wrapOrder(MaintainOrderRequest request)
    {
        com.todaysoft.ghealth.mybatis.model.Order data = new com.todaysoft.ghealth.mybatis.model.Order();
        Product product = new Product();
        product.setId(request.getProductId());
        data.setProduct(product);
        Customer customer = new Customer();
        customer.setId(request.getCustomerId());
        data.setCustomer(customer);
        data.setReportPrintRequired(request.getReportPrintRequired());
        data.setSampleType(request.getSampleType());
        data.setCode(request.getCode());
        data.setId(request.getId());
        data.setStatus(request.getStatus());
        data.setActualPrice(request.getPrice());
        return data;
    }
    
    public ListResponse<OrderHistory> getOrderHistories(MaintainOrderHistoryRequest request)
    {
        List<com.todaysoft.ghealth.mybatis.model.OrderHistory> list =
            orderHistoryService.getOrderHistories(request.getName(), request.getYear(), request.getMonth());
        return new ListResponse<>(wrapper.wrapOrderHistory(list));
    }
    
    public ListResponse<OrderHistory> getOrderHistoryLists(MaintainOrderHistoryRequest request)
    {
        List<com.todaysoft.ghealth.mybatis.model.OrderHistory> list =
            orderHistoryService.getOrderHistoryLists(request.getName(), request.getYear(), request.getMonth(), request.getDay());
        return new ListResponse<>(wrapper.wrapOrderHistory(list));
    }
    
    public ListResponse<OrderHistory> getOrderHistory(QueryOrderHistoryRequest request)
    {
        StatisticsSearcher searcher = new StatisticsSearcher();
        BeanUtils.copyProperties(request, searcher, "startTime", "endTime");
        wrapSearcherOrder(request, searcher);
        List<com.todaysoft.ghealth.mybatis.model.OrderHistory> list = orderHistoryService.getOrderHistory(searcher);
        return new ListResponse<>(wrapper.wrapOrderHistory(list));
    }
    
    private void wrapSearcherOrder(QueryOrderHistoryRequest request, StatisticsSearcher searcher)
    {
        if (StringUtils.isNotEmpty(request.getEndTime()))
        {
            searcher.setEndTime(transferLongToDate("yyyy-MM-dd 23:59:59", Long.valueOf(request.getEndTime())));
        }
        if (StringUtils.isNotEmpty(request.getStartTime()))
        {
            searcher.setStartTime(transferLongToDate("yyyy-MM-dd 00:00:00", Long.valueOf(request.getStartTime())));
        }
    }
    
    public ObjectResponse<String> getPath(MaintainOrderRequest request)
    {
        com.todaysoft.ghealth.mybatis.model.Order order = service.getOrderById(request.getId());
        
        if (StringUtils.isEmpty(order.getReportGenerateTaskId()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_DOWNLOAD_UNGENERATED);
        }
        
        ReportGenerateTask task = reportGenerateService.getReportGenerateTask(order.getReportGenerateTaskId());
        
        if (null == task || !ReportGenerateTask.STATUS_SUCCESS.equals(task.getStatus()))
        {
            throw new ServiceException(MgmtErrorCode.REPORT_DOWNLOAD_UNGENERATED);
        }
        
        String uri = task.getPdfFileUrl();
        
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        String path = ((ServletRequestAttributes)attributes).getRequest().getServletContext().getRealPath(uri);
        return new ObjectResponse<String>(path);
    }
    
    @Transactional
    public String cancel(MaintainOrderRequest request)
    {
        ManagementAccountDetails account = accountService.getManagementAccountDetails(request.getToken());
        com.todaysoft.ghealth.mybatis.model.Order data = service.getOrderById(request.getId());
        
        if (canCancel(data))
        {
            Agency agency = agencyService.get(data.getAgency().getId());
            AgencyBill agencyBill = new AgencyBill();
            BigDecimal accountAmount = agency.getAccountAmount().add(data.getActualPrice());
            agencyBill.setAmountBefore(agency.getAccountAmount());
            
            agencyBill.setAgency(agency);
            agencyBill.setBillType(DataStatus.BILL_REFUND);
            agencyBill.setIncreased(DataStatus.BALANCE_PLUS);
            agencyBill.setAmountAfter(accountAmount);
            agencyBill.setEventDetails(data.getId());
            agencyBill.setTitle("订单取消返还" + data.getActualPrice() + "元");
            agencyBill.setBillTime(new Date());
            agencyBillFacade.create(agencyBill);
            
            agency.setAccountAmount(accountAmount);
            agencyService.modify(agency, null);
        }
        data.setUpdateTime(new Date());
        data.setUpdatorName(account.getName());
        data.setStatus(DataStatus.ORDER_CANCELED);
        service.modify(data);
        return data.getId();
    }
    
    private boolean canCancel(com.todaysoft.ghealth.mybatis.model.Order data)
    {
        boolean flag = true;
        String status = data.getStatus();
        if (data.getActualPrice().compareTo(new BigDecimal(0)) == 0)
        {
            flag = false;
        }
        if (DataStatus.ORDER_DRAFT.equals(status))
        {
            flag = false;
        }
        return flag;
    }
    
    public ObjectResponse<Order> getByCode(MaintainOrderRequest request)
    {
        com.todaysoft.ghealth.mybatis.model.Order order = service.getByCode(request.getCode());
        return new ObjectResponse<Order>(wrapper.wrap(order));
    }
    
    public ObjectResponse<String> dataDetails(MaintainOrderRequest request)
    {
        String dataDetail = service.dataDetails(request.getId());
        return new ObjectResponse<String>(dataDetail);
    }
}
