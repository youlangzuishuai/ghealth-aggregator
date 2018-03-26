package com.todaysoft.ghealth.model.product;

public class ProductSearcher
{
    private String name;
    
    private String code;
    
    private String agencyId;
    
    private String agencyAccountId;
    
    private boolean isLogin = true;
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getCode()
    {
        return code;
    }
    
    public void setCode(String code)
    {
        this.code = code;
    }
    
    public String getAgencyId()
    {
        return agencyId;
    }
    
    public void setAgencyId(String agencyId)
    {
        this.agencyId = agencyId;
    }
    
    public String getAgencyAccountId()
    {
        return agencyAccountId;
    }
    
    public void setAgencyAccountId(String agencyAccountId)
    {
        this.agencyAccountId = agencyAccountId;
    }
    
    public boolean isLogin()
    {
        return isLogin;
    }
    
    public void setIsLogin(boolean login)
    {
        isLogin = login;
    }
}
