package com.todaysoft.ghealth.migrate.model;

public class User
{
    public static final String PA_TRUE = "0";
    
    public static final String PA_FALSE = "1";
    
    private String enable;
    
    private String id;
    
    private String name;
    
    private String userName;
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
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
    
    public String getEnable()
    {
        return enable;
    }
    
    public void setEnable(String enable)
    {
        this.enable = enable;
    }
}
