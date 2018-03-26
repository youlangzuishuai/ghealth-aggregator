package com.todaysoft.ghealth.mgmt.request;

import java.util.Map;

import com.todaysoft.ghealth.base.request.SignatureTokenListRequest;

public class QueryAgenciesRequest extends SignatureTokenListRequest
{
    private String code;
    
    private String name;
    
    private String productId;
    
    @Override
    protected void setSignFields(Map<String, String> fields)
    {
        super.setSignFields(fields);
        fields.put("code", code);
        fields.put("name", name);
        fields.put("productId", productId);
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getProductId()
    {
        return productId;
    }
    
    public void setProductId(String productId)
    {
        this.productId = productId;
    }
}