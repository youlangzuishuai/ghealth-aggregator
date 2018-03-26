package com.todaysoft.ghealth.migrate.model;

import java.math.BigDecimal;

public class Disease
{
    private String id;
    
    private String name;
    
    private BigDecimal maleRisk;
    
    private BigDecimal femaleRisk;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public BigDecimal getMaleRisk()
    {
        return maleRisk;
    }
    
    public void setMaleRisk(BigDecimal maleRisk)
    {
        this.maleRisk = maleRisk;
    }
    
    public BigDecimal getFemaleRisk()
    {
        return femaleRisk;
    }
    
    public void setFemaleRisk(BigDecimal femaleRisk)
    {
        this.femaleRisk = femaleRisk;
    }
}
