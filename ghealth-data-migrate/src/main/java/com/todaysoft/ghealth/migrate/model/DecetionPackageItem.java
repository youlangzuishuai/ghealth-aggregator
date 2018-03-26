package com.todaysoft.ghealth.migrate.model;

public class DecetionPackageItem
{
    public static final String MALE = "1";
    
    public static final String FEMALE = "2";

    public static final String GLOBAL = "0";

    private String packageId;
    
    private String itemId;
    
    private String gender;
    
    public String getGender()
    {
        return gender;
    }
    
    public void setGender(String gender)
    {
        this.gender = gender;
    }
    
    public String getPackageId()
    {
        return packageId;
    }
    
    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }
    
    public String getItemId()
    {
        return itemId;
    }
    
    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
    
}
