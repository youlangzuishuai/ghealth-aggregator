package com.todaysoft.ghealth.migrate.model;

import java.math.BigDecimal;

public class OrderEvalItem
{
    private String reportId;
    
    private String itemKey;
    
    private BigDecimal risk;
    
    public String getReportId()
    {
        return reportId;
    }
    
    public void setReportId(String reportId)
    {
        this.reportId = reportId;
    }
    
    public String getItemKey()
    {
        return itemKey;
    }
    
    public void setItemKey(String itemKey)
    {
        this.itemKey = itemKey;
    }
    
    public BigDecimal getRisk()
    {
        return risk;
    }
    
    public void setRisk(BigDecimal risk)
    {
        this.risk = risk;
    }
}
