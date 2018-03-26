package com.todaysoft.ghealth.open.api.mybatis.model;

import java.util.List;

public class OrderQuery extends Query
{
    private String orderCode;
    
    private List<String> orderStatusIncludes;
    
    private List<String> orderStatusExcludes;
    
    private String productName;
    
    private List<String> productKeyIncludes;
    
    private List<String> productKeyExcludes;
    
    private String agencyName;
    
    private String customerName;
    
    public String getOrderCode()
    {
        return orderCode;
    }
    
    public void setOrderCode(String orderCode)
    {
        this.orderCode = orderCode;
    }
    
    public List<String> getOrderStatusIncludes()
    {
        return orderStatusIncludes;
    }
    
    public void setOrderStatusIncludes(List<String> orderStatusIncludes)
    {
        this.orderStatusIncludes = orderStatusIncludes;
    }
    
    public List<String> getOrderStatusExcludes()
    {
        return orderStatusExcludes;
    }
    
    public void setOrderStatusExcludes(List<String> orderStatusExcludes)
    {
        this.orderStatusExcludes = orderStatusExcludes;
    }
    
    public String getProductName()
    {
        return productName;
    }
    
    public void setProductName(String productName)
    {
        this.productName = productName;
    }
    
    public List<String> getProductKeyIncludes()
    {
        return productKeyIncludes;
    }
    
    public void setProductKeyIncludes(List<String> productKeyIncludes)
    {
        this.productKeyIncludes = productKeyIncludes;
    }
    
    public List<String> getProductKeyExcludes()
    {
        return productKeyExcludes;
    }
    
    public void setProductKeyExcludes(List<String> productKeyExcludes)
    {
        this.productKeyExcludes = productKeyExcludes;
    }
    
    public String getAgencyName()
    {
        return agencyName;
    }
    
    public void setAgencyName(String agencyName)
    {
        this.agencyName = agencyName;
    }
    
    public String getCustomerName()
    {
        return customerName;
    }
    
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }
}
