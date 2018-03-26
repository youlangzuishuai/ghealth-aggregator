package com.todaysoft.ghealth.mgmt.request;

import java.util.Map;

import com.todaysoft.ghealth.base.request.SignatureTokenRequest;

public class DownloadOrderReportRequest extends SignatureTokenRequest
{
    private String orderId;
    
    private String type;
    
    @Override
    protected void setSignFields(Map<String, String> fields)
    {
        super.setSignFields(fields);
        fields.put("orderId", orderId);
        fields.put("type", type);
    }
    
    public String getOrderId()
    {
        return orderId;
    }
    
    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
    
    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
}
