package com.todaysoft.ghealth.mgmt.request;

import java.util.Map;

import com.todaysoft.ghealth.base.request.SignatureTokenListRequest;

public class QueryCustomersRequest extends SignatureTokenListRequest
{
    private String customerName;
    
    private String customerPhone;
    
    private String agencyName;
    
    private String agencyId;
    
    @Override
    protected void setSignFields(Map<String, String> fields)
    {
        super.setSignFields(fields);
        fields.put("customerName", customerName);
        fields.put("customerPhone", customerPhone);
        fields.put("agencyName", agencyName);
        fields.put("agencyId", agencyId);
    }
    
    public String getCustomerName()
    {
        return customerName;
    }
    
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }
    
    public String getCustomerPhone()
    {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone)
    {
        this.customerPhone = customerPhone;
    }
    
    public String getAgencyName()
    {
        return agencyName;
    }
    
    public void setAgencyName(String agencyName)
    {
        this.agencyName = agencyName;
    }
    
    public String getAgencyId()
    {
        return agencyId;
    }
    
    public void setAgencyId(String agencyId)
    {
        this.agencyId = agencyId;
    }
}