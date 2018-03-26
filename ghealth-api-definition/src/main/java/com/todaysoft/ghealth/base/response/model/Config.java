package com.todaysoft.ghealth.base.response.model;

public class Config
{
    private String id;
    
    private String configKey;
    
    private String configName;
    
    private String configValue;
    
    private Boolean configValueType;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getConfigKey()
    {
        return configKey;
    }
    
    public void setConfigKey(String configKey)
    {
        this.configKey = configKey;
    }
    
    public String getConfigName()
    {
        return configName;
    }
    
    public void setConfigName(String configName)
    {
        this.configName = configName;
    }
    
    public String getConfigValue()
    {
        return configValue;
    }
    
    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
    }
    
    public Boolean getConfigValueType()
    {
        return configValueType;
    }
    
    public void setConfigValueType(Boolean configValueType)
    {
        this.configValueType = configValueType;
    }
}