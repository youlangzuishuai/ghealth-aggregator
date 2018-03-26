package com.todaysoft.ghealth.migrate.model;

import java.math.BigDecimal;

public class OrderEvalItemLocus
{
    private String reportId;
    
    private String locusCode;
    
    private String gengType;
    
    private BigDecimal risk;
    
    private String itemId;
    
    private String result;
    
    public BigDecimal getRisk()
    {
        return risk;
    }
    
    public void setRisk(BigDecimal risk)
    {
        this.risk = risk;
    }
    
    public String getItemId()
    {
        return itemId;
    }
    
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
    
    public String getResult()
    {
        return result;
    }
    
    public void setResult(String result)
    {
        this.result = result;
    }
    
    public String getReportId()
    {
        return reportId;
    }
    
    public void setReportId(String reportId)
    {
        this.reportId = reportId;
    }
    
    public String getLocusCode()
    {
        return locusCode;
    }
    
    public void setLocusCode(String locusCode)
    {
        this.locusCode = locusCode;
    }
    
    public String getGengType()
    {
        return gengType;
    }
    
    public void setGengType(String gengType)
    {
        this.gengType = gengType;
    }
}
