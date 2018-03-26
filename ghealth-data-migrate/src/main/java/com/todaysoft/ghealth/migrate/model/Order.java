package com.todaysoft.ghealth.migrate.model;

import java.sql.Timestamp;

public class Order
{
    private String id;
    
    private String orderCode;
    
    private String customerId;
    
    private String customerName;
    
    private String productId;
    
    private String agencyId;
    
    private String sampleType;
    
    private String status;
    
    private String createName;
    
    private String isPaperReport;
    
    private Timestamp submitTime;
    
    private OrderTask orderTask;
    
    private String createId;
    
    private Timestamp createTime;
    
    public String getCustomerName()
    {
        return customerName;
    }
    
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }
    
    public Timestamp getCreateTime()
    {
        return createTime;
    }
    
    public void setCreateTime(Timestamp createTime)
    {
        this.createTime = createTime;
    }
    
    public String getCreateId()
    {
        return createId;
    }
    
    public void setCreateId(String createId)
    {
        this.createId = createId;
    }
    
    public OrderTask getOrderTask()
    {
        return orderTask;
    }
    
    public void setOrderTask(OrderTask orderTask)
    {
        this.orderTask = orderTask;
    }
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getOrderCode()
    {
        return orderCode;
    }
    
    public void setOrderCode(String orderCode)
    {
        this.orderCode = orderCode;
    }
    
    public String getCustomerId()
    {
        return customerId;
    }
    
    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }
    
    public String getProductId()
    {
        return productId;
    }
    
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
    
    public String getAgencyId()
    {
        return agencyId;
    }
    
    public void setAgencyId(String agencyId)
    {
        this.agencyId = agencyId;
    }
    
    public String getSampleType()
    {
        return sampleType;
    }
    
    public void setSampleType(String sampleType)
    {
        this.sampleType = sampleType;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public String getCreateName()
    {
        return createName;
    }
    
    public void setCreateName(String createName)
    {
        this.createName = createName;
    }
    
    public String getIsPaperReport()
    {
        return isPaperReport;
    }
    
    public void setIsPaperReport(String isPaperReport)
    {
        this.isPaperReport = isPaperReport;
    }
    
    public Timestamp getSubmitTime()
    {
        return submitTime;
    }
    
    public void setSubmitTime(Timestamp submitTime)
    {
        this.submitTime = submitTime;
    }
}
