package com.todaysoft.ghealth.migrate.model;

import java.math.BigDecimal;

public class ItemLocus
{
    
    private String gender;
    
    private String itemId;
    
    private String locusCode;
    
    private String geneType;
    
    private BigDecimal risk;
    
    public String getGender()
    {
        return gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }
    
    public String getItemId()
    {
        return itemId;
    }
    
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
    
    public String getLocusCode()
    {
        return locusCode;
    }
    
    public void setLocusCode(String locusCode)
    {
        this.locusCode = locusCode;
    }
    
    public String getGeneType()
    {
        return geneType;
    }
    
    public void setGeneType(String geneType)
    {
        this.geneType = geneType;
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
